<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Commandes" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h1 class="h3 mb-0">Commandes</h1>
    <form method="get" action="${ctx}/admin/commandes" class="d-flex gap-2">
        <select name="statut" class="form-select" aria-label="Filtrer par statut" onchange="this.form.submit()">
            <option value="">Tous les statuts</option>
            <c:forEach items="${statuts}" var="s">
                <option value="${s}" ${s == statutFiltre ? 'selected' : ''}><c:out value="${s.libelle}"/></option>
            </c:forEach>
        </select>
        <noscript><button type="submit" class="btn btn-outline-primary">Filtrer</button></noscript>
    </form>
</div>

<c:choose>
    <c:when test="${empty commandes}">
        <p class="text-muted">Aucune commande<c:if test="${not empty statutFiltre}"> avec le statut « <c:out value="${statutFiltre.libelle}"/> »</c:if>.</p>
    </c:when>
    <c:otherwise>
        <p class="small text-muted"><c:out value="${total}"/> commande(s)</p>
        <div class="table-responsive">
            <table class="table table-striped align-middle bg-white">
                <thead>
                <tr>
                    <th>N°</th><th>Date</th><th>Client</th><th class="text-end">Montant</th>
                    <th>Statut</th><th class="text-end">Changer le statut</th><th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${commandes}" var="commande">
                    <tr>
                        <td class="fw-semibold"><c:out value="${commande.id}"/></td>
                        <td class="text-nowrap"><c:out value="${commande.dateCommandeFormatee}"/></td>
                        <td>
                            <c:out value="${commande.utilisateur.prenom} ${commande.utilisateur.nom}"/>
                            <div class="small text-muted"><c:out value="${commande.utilisateur.email}"/></div>
                        </td>
                        <td class="text-end text-nowrap">
                            <fmt:formatNumber value="${commande.montantTotal}" minFractionDigits="2" maxFractionDigits="2"/> DH
                        </td>
                        <td>
                            <c:set var="statutAffiche" value="${commande.statut}" scope="request"/>
                            <jsp:include page="/WEB-INF/views/commun/statut.jsp"/>
                        </td>
                        <td class="text-end">
                            <c:set var="commande" value="${commande}" scope="request"/>
                            <jsp:include page="/WEB-INF/views/admin/commande-statut-form.jsp"/>
                        </td>
                        <td class="text-end">
                            <a class="btn btn-sm btn-outline-secondary"
                               href="${ctx}/admin/commandes?id=${commande.id}">Détail</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <jsp:include page="/WEB-INF/views/commun/pagination.jsp"/>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
