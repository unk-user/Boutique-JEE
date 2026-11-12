<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Utilisateurs" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%-- BF22 : liste des comptes, activation et désactivation --%>
<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h1 class="h3 mb-0">Utilisateurs</h1>
    <span class="small text-muted"><c:out value="${total}"/> compte(s)</span>
</div>

<div class="table-responsive">
    <table class="table table-striped align-middle bg-white">
        <thead>
        <tr>
            <th>Nom</th><th>E-mail</th><th>Téléphone</th><th>Rôle</th>
            <th>Inscription</th><th>État</th><th class="text-end">Action</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${utilisateurs}" var="u">
            <tr class="${u.actif ? '' : 'text-muted'}">
                <td class="fw-semibold"><c:out value="${u.prenom} ${u.nom}"/></td>
                <td><c:out value="${u.email}"/></td>
                <td class="text-nowrap"><c:out value="${u.telephone}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${u.role == 'ADMIN'}"><span class="badge text-bg-dark">Administrateur</span></c:when>
                        <c:otherwise><span class="badge text-bg-light border">Client</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="text-nowrap"><c:out value="${u.dateInscriptionFormatee}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${u.actif}"><span class="badge text-bg-success">Actif</span></c:when>
                        <c:otherwise><span class="badge text-bg-secondary">Désactivé</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="text-end text-nowrap">
                    <c:choose>
                        <%-- Un administrateur ne peut pas être désactivé (le service le refuse aussi) --%>
                        <c:when test="${u.role == 'ADMIN'}"><span class="small text-muted">—</span></c:when>
                        <c:otherwise>
                            <form method="post" action="${ctx}/admin/utilisateurs" class="d-inline"
                                  <c:if test="${u.actif}">onsubmit="return confirm('Désactiver ce compte ? L\'utilisateur ne pourra plus se connecter.');"</c:if>>
                                <input type="hidden" name="id" value="${u.id}">
                                <input type="hidden" name="page" value="${pageCourante}">
                                <input type="hidden" name="action" value="${u.actif ? 'desactiver' : 'activer'}">
                                <button type="submit" class="btn btn-sm ${u.actif ? 'btn-outline-danger' : 'btn-outline-success'}">
                                    ${u.actif ? 'Désactiver' : 'Activer'}
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<jsp:include page="/WEB-INF/views/commun/pagination.jsp"/>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
