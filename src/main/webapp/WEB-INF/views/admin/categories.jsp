<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Catégories" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h1 class="h3 mb-0">Catégories</h1>
    <a class="btn btn-primary" href="${ctx}/admin/categories?action=nouveau">Nouvelle catégorie</a>
</div>

<c:choose>
    <c:when test="${empty categories}">
        <p class="text-muted">Aucune catégorie pour le moment.</p>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-striped align-middle bg-white">
                <thead>
                <tr><th>Nom</th><th>Description</th><th class="text-end">Actions</th></tr>
                </thead>
                <tbody>
                <c:forEach items="${categories}" var="categorie">
                    <tr>
                        <td class="fw-semibold"><c:out value="${categorie.nom}"/></td>
                        <td class="text-muted"><c:out value="${categorie.description}"/></td>
                        <td class="text-end text-nowrap">
                            <a class="btn btn-sm btn-outline-secondary"
                               href="${ctx}/admin/categories?action=modifier&amp;id=${categorie.id}">Modifier</a>
                            <form method="post" action="${ctx}/admin/categories" class="d-inline"
                                  onsubmit="return confirm('Supprimer cette catégorie ?');">
                                <input type="hidden" name="action" value="supprimer">
                                <input type="hidden" name="id" value="${categorie.id}">
                                <button type="submit" class="btn btn-sm btn-outline-danger">Supprimer</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
