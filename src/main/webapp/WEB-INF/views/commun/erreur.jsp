<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  Page d'erreur commune (400, 403, 404, 405, 500), déclarée dans web.xml.
  Aucun détail technique n'est affiché : la cause reste dans les journaux du serveur.
--%>
<c:set var="code" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<c:choose>
    <c:when test="${code == 400}">
        <c:set var="intitule" value="Requête incorrecte"/>
        <c:set var="message" value="Le serveur n'a pas pu comprendre la requête. Vérifiez l'adresse saisie."/>
    </c:when>
    <c:when test="${code == 403}">
        <c:set var="intitule" value="Accès refusé"/>
        <c:set var="message" value="Vous n'avez pas les droits nécessaires pour consulter cette page."/>
    </c:when>
    <c:when test="${code == 404}">
        <c:set var="intitule" value="Page introuvable"/>
        <c:set var="message" value="La page demandée n'existe pas ou a été déplacée."/>
    </c:when>
    <c:when test="${code == 405}">
        <c:set var="intitule" value="Méthode non autorisée"/>
        <c:set var="message" value="Cette page ne peut pas être appelée de cette façon. Utilisez les boutons et les liens du site."/>
    </c:when>
    <c:otherwise>
        <c:set var="code" value="500"/>
        <c:set var="intitule" value="Erreur interne"/>
        <c:set var="message" value="Une erreur est survenue sur le serveur. Veuillez réessayer dans quelques instants."/>
    </c:otherwise>
</c:choose>
<c:set var="titre" value="${intitule}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="row justify-content-center">
    <div class="col-sm-10 col-md-8 col-lg-6">
        <div class="card shadow-sm text-center">
            <div class="card-body p-5">
                <p class="display-3 fw-bold text-secondary mb-0"><c:out value="${code}"/></p>
                <h1 class="h4 mb-3"><c:out value="${intitule}"/></h1>
                <p class="text-muted"><c:out value="${message}"/></p>
                <a class="btn btn-primary" href="${ctx}/catalogue">Retour au catalogue</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
