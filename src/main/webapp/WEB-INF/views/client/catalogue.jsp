<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Catalogue" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<h1 class="h3 mb-3">Catalogue</h1>

<%-- BF06, BF07 : filtre par catégorie et recherche par mot-clé (GET : l'URL reste partageable) --%>
<form method="get" action="${ctx}/catalogue" class="row g-2 mb-4">
    <div class="col-12 col-md-4">
        <label for="categorie" class="visually-hidden">Catégorie</label>
        <select id="categorie" name="categorie" class="form-select">
            <option value="">Toutes les catégories</option>
            <c:forEach items="${categories}" var="categorie">
                <option value="${categorie.id}" ${categorie.id == categorieId ? 'selected' : ''}>
                    <c:out value="${categorie.nom}"/>
                </option>
            </c:forEach>
        </select>
    </div>
    <div class="col-12 col-md-5">
        <label for="motCle" class="visually-hidden">Mot-clé</label>
        <input type="search" id="motCle" name="motCle" value="<c:out value='${motCle}'/>" maxlength="100"
               class="form-control" placeholder="Rechercher un produit (nom, description)">
    </div>
    <div class="col-12 col-md-3 d-flex gap-2">
        <button type="submit" class="btn btn-primary flex-grow-1">Rechercher</button>
        <c:if test="${not empty categorieId or not empty motCle}">
            <a class="btn btn-outline-secondary" href="${ctx}/catalogue">Réinitialiser</a>
        </c:if>
    </div>
</form>

<c:choose>
    <c:when test="${empty produits}">
        <div class="alert alert-info" role="alert">
            <c:choose>
                <c:when test="${not empty categorieId or not empty motCle}">Aucun produit ne correspond à votre recherche.</c:when>
                <c:otherwise>Aucun produit n'est disponible pour le moment.</c:otherwise>
            </c:choose>
        </div>
    </c:when>
    <c:otherwise>
        <p class="text-muted small">
            <c:out value="${total}"/> produit<c:if test="${total > 1}">s</c:if>
        </p>
        <div class="row row-cols-1 row-cols-sm-2 row-cols-lg-3 row-cols-xl-4 g-3 mb-4">
            <c:forEach items="${produits}" var="produit">
                <div class="col">
                    <div class="card h-100 shadow-sm">
                        <a href="${ctx}/produit?id=${produit.id}" class="text-decoration-none">
                            <c:choose>
                                <c:when test="${not empty produit.imageUrl}">
                                    <img src="<c:out value='${produit.imageUrl}'/>" alt="<c:out value='${produit.nom}'/>"
                                         class="card-img-top object-fit-cover" style="height: 180px">
                                </c:when>
                                <c:otherwise>
                                    <div class="bg-secondary-subtle text-secondary d-flex align-items-center justify-content-center"
                                         style="height: 180px">Pas d'image</div>
                                </c:otherwise>
                            </c:choose>
                        </a>
                        <div class="card-body d-flex flex-column">
                            <div class="small text-muted"><c:out value="${produit.categorie.nom}"/></div>
                            <h2 class="h6 card-title">
                                <a href="${ctx}/produit?id=${produit.id}" class="text-decoration-none text-dark stretched-link">
                                    <c:out value="${produit.nom}"/>
                                </a>
                            </h2>
                            <div class="mt-auto d-flex justify-content-between align-items-center">
                                <span class="fw-bold text-nowrap">
                                    <fmt:formatNumber value="${produit.prix}" minFractionDigits="2" maxFractionDigits="2"/> DH
                                </span>
                                <%-- RG06 : un produit en rupture reste visible --%>
                                <c:if test="${produit.stock == 0}"><span class="badge text-bg-warning">Rupture</span></c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
        <jsp:include page="/WEB-INF/views/commun/pagination.jsp"/>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
