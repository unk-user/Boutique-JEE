<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="${produit.nom}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<nav aria-label="Fil d'Ariane">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="${ctx}/catalogue">Catalogue</a></li>
        <li class="breadcrumb-item">
            <a href="${ctx}/catalogue?categorie=${produit.categorie.id}"><c:out value="${produit.categorie.nom}"/></a>
        </li>
        <li class="breadcrumb-item active" aria-current="page"><c:out value="${produit.nom}"/></li>
    </ol>
</nav>

<div class="row g-4">
    <div class="col-12 col-md-5">
        <c:choose>
            <c:when test="${not empty produit.imageUrl}">
                <img src="<c:out value='${produit.imageUrl}'/>" alt="<c:out value='${produit.nom}'/>"
                     class="img-fluid rounded w-100 object-fit-cover" style="max-height: 420px">
            </c:when>
            <c:otherwise>
                <div class="bg-secondary-subtle text-secondary rounded d-flex align-items-center justify-content-center"
                     style="height: 320px">Pas d'image</div>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="col-12 col-md-7">
        <h1 class="h3"><c:out value="${produit.nom}"/></h1>
        <p class="text-muted">Catégorie : <c:out value="${produit.categorie.nom}"/></p>
        <p class="fs-4 fw-bold">
            <fmt:formatNumber value="${produit.prix}" minFractionDigits="2" maxFractionDigits="2"/> DH
        </p>
        <p>
            <c:choose>
                <c:when test="${produit.stock == 0}"><span class="badge text-bg-warning fs-6">Rupture</span></c:when>
                <c:otherwise>
                    <span class="badge text-bg-success fs-6">En stock</span>
                    <span class="text-muted ms-2"><c:out value="${produit.stock}"/> disponible<c:if test="${produit.stock > 1}">s</c:if></span>
                </c:otherwise>
            </c:choose>
        </p>
        <c:if test="${not empty produit.description}">
            <h2 class="h6">Description</h2>
            <p style="white-space: pre-line"><c:out value="${produit.description}"/></p>
        </c:if>

        <c:choose>
            <c:when test="${empty sessionScope.utilisateur}">
                <a class="btn btn-primary" href="${ctx}/connexion">Connectez-vous pour commander</a>
            </c:when>
            <c:when test="${sessionScope.utilisateur.role == 'CLIENT' and produit.stock > 0}">
            </c:when>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
