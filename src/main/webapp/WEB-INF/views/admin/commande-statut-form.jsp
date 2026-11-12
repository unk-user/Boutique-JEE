<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="suivants" value="${transitions[commande.statut]}"/>
<c:choose>
    <c:when test="${empty suivants}">
        <span class="small text-muted">Statut définitif</span>
    </c:when>
    <c:otherwise>
        <form method="post" action="${ctx}/admin/commandes" class="d-flex gap-1 justify-content-end"
              onsubmit="return this.nouveauStatut.value !== 'ANNULEE' || confirm('Annuler la commande n° ${commande.id} ? Les quantités seront remises en stock.');">
            <input type="hidden" name="commandeId" value="${commande.id}">
            <input type="hidden" name="depuis" value="<c:out value='${depuis}'/>">
            <input type="hidden" name="statut" value="<c:out value='${statutFiltre}'/>">
            <input type="hidden" name="page" value="<c:out value='${pageCourante}'/>">
            <select name="nouveauStatut" class="form-select form-select-sm w-auto"
                    aria-label="Nouveau statut de la commande ${commande.id}">
                <c:forEach items="${suivants}" var="s">
                    <option value="${s}"><c:out value="${s.libelle}"/></option>
                </c:forEach>
            </select>
            <button type="submit" class="btn btn-sm btn-primary">Appliquer</button>
        </form>
    </c:otherwise>
</c:choose>
