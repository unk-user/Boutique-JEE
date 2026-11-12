<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Mes commandes" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<h1 class="h3 mb-3">Mes commandes</h1>
<p class="text-muted">L'historique de vos commandes sera bientôt disponible sur cette page.</p>
<a class="btn btn-primary" href="${ctx}/catalogue">Continuer mes achats</a>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
