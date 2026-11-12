<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Commande n° ${commande.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h1 class="h3 mb-0">Commande n° <c:out value="${commande.id}"/></h1>
    <a class="btn btn-outline-secondary" href="${ctx}/admin/commandes">Retour aux commandes</a>
</div>

<div class="card shadow-sm mb-4">
    <div class="card-body d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
            <div class="fw-semibold">
                <c:out value="${commande.utilisateur.prenom} ${commande.utilisateur.nom}"/>
            </div>
            <div class="small text-muted">
                <c:out value="${commande.utilisateur.email}"/>
                <c:if test="${not empty commande.utilisateur.telephone}"> · <c:out value="${commande.utilisateur.telephone}"/></c:if>
            </div>
        </div>
        <div>
            <c:set var="depuis" value="detail" scope="request"/>
            <jsp:include page="/WEB-INF/views/admin/commande-statut-form.jsp"/>
        </div>
    </div>
</div>

<c:set var="statutAffiche" value="${commande.statut}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/commande-resume.jsp"/>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
