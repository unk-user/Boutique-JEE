<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Commande n° ${commande.id}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%-- BF15 : détail d'une commande du client (RG16 vérifiée par le service) --%>
<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h1 class="h3 mb-0">Commande n° <c:out value="${commande.id}"/></h1>
    <div class="d-flex flex-wrap gap-2">
        <%-- BF16 : annulation possible tant que la commande est « En attente » --%>
        <c:if test="${commande.statut == 'EN_ATTENTE'}">
            <form method="post" action="${ctx}/client/commandes"
                  onsubmit="return confirm('Annuler la commande n° ${commande.id} ? Les articles seront remis en stock.');">
                <input type="hidden" name="action" value="annuler">
                <input type="hidden" name="commandeId" value="${commande.id}">
                <button type="submit" class="btn btn-outline-danger">Annuler la commande</button>
            </form>
        </c:if>
        <a class="btn btn-outline-secondary" href="${ctx}/client/commandes">Retour à mes commandes</a>
    </div>
</div>

<c:set var="statutAffiche" value="${commande.statut}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/commande-resume.jsp"/>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
