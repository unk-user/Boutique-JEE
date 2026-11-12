<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Commande n° ${commande.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h1 class="h3 mb-0">Commande n° <c:out value="${commande.id}"/></h1>
    <a class="btn btn-outline-secondary" href="${ctx}/client/commandes">Retour à mes commandes</a>
</div>

<c:set var="statutAffiche" value="${commande.statut}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/commande-resume.jsp"/>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
