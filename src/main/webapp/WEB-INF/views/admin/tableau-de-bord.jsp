<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Tableau de bord" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<h1 class="h3 mb-3">Tableau de bord</h1>
<p class="text-muted">Bienvenue, <c:out value="${sessionScope.utilisateur.prenom}"/>.</p>

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
</div>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
