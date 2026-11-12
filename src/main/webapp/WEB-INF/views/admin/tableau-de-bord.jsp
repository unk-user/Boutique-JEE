<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Tableau de bord" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>

<h1 class="h3 mb-3">Tableau de bord</h1>
<p class="text-muted">
    Bienvenue, <c:out value="${sessionScope.utilisateur.prenom}"/>. Les outils d'administration seront ajoutés ici.
</p>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
