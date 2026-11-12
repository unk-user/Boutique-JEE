<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:choose>
    <c:when test="${statutAffiche == 'EN_ATTENTE'}"><c:set var="couleur" value="text-bg-warning"/></c:when>
    <c:when test="${statutAffiche == 'VALIDEE'}"><c:set var="couleur" value="text-bg-info"/></c:when>
    <c:when test="${statutAffiche == 'EXPEDIEE'}"><c:set var="couleur" value="text-bg-primary"/></c:when>
    <c:when test="${statutAffiche == 'LIVREE'}"><c:set var="couleur" value="text-bg-success"/></c:when>
    <c:otherwise><c:set var="couleur" value="text-bg-danger"/></c:otherwise>
</c:choose>
<span class="badge ${couleur}"><c:out value="${statutAffiche.libelle}"/></span>
