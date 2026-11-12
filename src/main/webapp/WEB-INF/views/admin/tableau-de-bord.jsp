<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Tableau de bord" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<h1 class="h3 mb-3">Tableau de bord</h1>
<p class="text-muted">Bienvenue, <c:out value="${sessionScope.utilisateur.prenom}"/>.</p>

<%-- BF23 : indicateurs --%>
<div class="row g-3 mb-4">
    <div class="col-12 col-md-4">
        <div class="card h-100 shadow-sm">
            <div class="card-body">
                <div class="text-muted small">Commandes</div>
                <div class="display-6 fw-bold"><c:out value="${nombreCommandes}"/></div>
            </div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="card h-100 shadow-sm">
            <div class="card-body">
                <div class="text-muted small">Chiffre d'affaires</div>
                <div class="display-6 fw-bold text-nowrap">
                    <fmt:formatNumber value="${chiffreAffaires}" minFractionDigits="2" maxFractionDigits="2"/> DH
                </div>
                <div class="text-muted small">Commandes annulées exclues</div>
            </div>
        </div>
    </div>
    <div class="col-12 col-md-4">
        <div class="card h-100 shadow-sm">
            <div class="card-body">
                <div class="text-muted small">Produits en rupture</div>
                <div class="display-6 fw-bold"><c:out value="${produitsEnRupture.size()}"/></div>
                <div class="text-muted small">Produits actifs dont le stock est à 0</div>
            </div>
        </div>
    </div>
</div>

<div class="row g-3 mb-4">
    <div class="col-lg-5">
        <div class="card h-100 shadow-sm">
            <div class="card-body">
                <h2 class="h5">Commandes par statut</h2>
                <ul class="list-group list-group-flush">
                    <c:forEach items="${commandesParStatut}" var="entree">
                        <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                            <a class="text-decoration-none" href="${ctx}/admin/commandes?statut=${entree.key}">
                                <c:set var="statutAffiche" value="${entree.key}" scope="request"/>
                                <jsp:include page="/WEB-INF/views/commun/statut.jsp"/>
                            </a>
                            <span class="fw-semibold"><c:out value="${entree.value}"/></span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
    <div class="col-lg-7">
        <div class="card h-100 shadow-sm">
            <div class="card-body">
                <h2 class="h5">Produits en rupture de stock</h2>
                <c:choose>
                    <c:when test="${empty produitsEnRupture}">
                        <p class="text-muted mb-0">Aucun produit en rupture.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-sm align-middle mb-0">
                                <thead><tr><th>Produit</th><th>Catégorie</th><th></th></tr></thead>
                                <tbody>
                                <c:forEach items="${produitsEnRupture}" var="produit">
                                    <tr>
                                        <td><c:out value="${produit.nom}"/></td>
                                        <td class="text-muted"><c:out value="${produit.categorie.nom}"/></td>
                                        <td class="text-end">
                                            <a class="btn btn-sm btn-outline-primary"
                                               href="${ctx}/admin/produits?action=modifier&amp;id=${produit.id}">Réapprovisionner</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<div class="row g-3">
    <div class="col-sm-6 col-lg-4">
        <a class="card h-100 text-decoration-none shadow-sm" href="${ctx}/admin/categories">
            <div class="card-body">
                <h2 class="h5">Catégories</h2>
                <p class="text-muted mb-0">Créer, modifier et supprimer les catégories.</p>
            </div>
        </a>
    </div>
    <div class="col-sm-6 col-lg-4">
        <a class="card h-100 text-decoration-none shadow-sm" href="${ctx}/admin/produits">
            <div class="card-body">
                <h2 class="h5">Produits</h2>
                <p class="text-muted mb-0">Gérer les produits et leur stock.</p>
            </div>
        </a>
    </div>
    <div class="col-sm-6 col-lg-4">
        <a class="card h-100 text-decoration-none shadow-sm" href="${ctx}/admin/utilisateurs">
            <div class="card-body">
                <h2 class="h5">Utilisateurs</h2>
                <p class="text-muted mb-0">Consulter les comptes, les activer ou les désactiver.</p>
            </div>
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
