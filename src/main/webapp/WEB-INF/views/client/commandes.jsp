<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Mes commandes" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<h1 class="h3 mb-3">Mes commandes</h1>

<c:choose>
    <c:when test="${empty commandes}">
        <div class="alert alert-info" role="alert">Vous n'avez encore passé aucune commande.</div>
        <a class="btn btn-primary" href="${ctx}/catalogue">Découvrir le catalogue</a>
    </c:when>
    <c:otherwise>
        <div class="table-responsive bg-white rounded shadow-sm">
            <table class="table align-middle mb-0">
                <thead>
                    <tr>
                        <th>N°</th>
                        <th>Date</th>
                        <th class="text-end">Montant</th>
                        <th>Statut</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${commandes}" var="commande">
                        <tr>
                            <td class="fw-semibold"><c:out value="${commande.id}"/></td>
                            <td class="text-nowrap"><c:out value="${commande.dateCommandeFormatee}"/></td>
                            <td class="text-end text-nowrap">
                                <fmt:formatNumber value="${commande.montantTotal}" minFractionDigits="2" maxFractionDigits="2"/> DH
                            </td>
                            <td>
                                <c:set var="statutAffiche" value="${commande.statut}" scope="request"/>
                                <jsp:include page="/WEB-INF/views/commun/statut.jsp"/>
                            </td>
                            <td class="text-end">
                                <a class="btn btn-sm btn-outline-primary"
                                   href="${ctx}/client/commandes?id=${commande.id}">Détail</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="mt-3">
            <jsp:include page="/WEB-INF/views/commun/pagination.jsp"/>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
