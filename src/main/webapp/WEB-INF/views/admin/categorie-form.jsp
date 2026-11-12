<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="${empty categorie.id ? 'Nouvelle catégorie' : 'Modifier la catégorie'}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="row justify-content-center">
    <div class="col-md-10 col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-4"><c:out value="${titre}"/></h1>
                <form method="post" action="${ctx}/admin/categories">
                    <input type="hidden" name="action" value="enregistrer">
                    <input type="hidden" name="id" value="${categorie.id}">
                    <div class="mb-3">
                        <label for="nom" class="form-label">Nom *</label>
                        <input type="text" class="form-control" id="nom" name="nom" required maxlength="100"
                               value="<c:out value='${categorie.nom}'/>">
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Description</label>
                        <textarea class="form-control" id="description" name="description" rows="3"
                                  maxlength="500"><c:out value="${categorie.description}"/></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Enregistrer</button>
                    <a class="btn btn-link" href="${ctx}/admin/categories">Annuler</a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
