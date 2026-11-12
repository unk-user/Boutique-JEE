<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="${empty produit.id ? 'Nouveau produit' : 'Modifier le produit'}" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="row justify-content-center">
    <div class="col-md-10 col-lg-8">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-4"><c:out value="${titre}"/></h1>
                <c:if test="${empty categories}">
                    <div class="alert alert-warning">
                        Aucune catégorie n'existe :
                        <a href="${ctx}/admin/categories?action=nouveau">créez-en une</a> avant d'ajouter un produit.
                    </div>
                </c:if>
                <form method="post" action="${ctx}/admin/produits">
                    <input type="hidden" name="action" value="enregistrer">
                    <input type="hidden" name="id" value="${produit.id}">
                    <div class="row g-3">
                        <div class="col-12">
                            <label for="nom" class="form-label">Nom *</label>
                            <input type="text" class="form-control" id="nom" name="nom" required maxlength="150"
                                   value="<c:out value='${produit.nom}'/>">
                        </div>
                        <div class="col-sm-6">
                            <label for="categorieId" class="form-label">Catégorie *</label>
                            <select class="form-select" id="categorieId" name="categorieId" required>
                                <option value="">Choisir…</option>
                                <c:forEach items="${categories}" var="categorie">
                                    <option value="${categorie.id}" ${categorie.id == categorieId ? 'selected' : ''}>
                                        <c:out value="${categorie.nom}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-6 col-sm-3">
                            <label for="prix" class="form-label">Prix (DH) *</label>
                            <input type="text" inputmode="decimal" class="form-control" id="prix" name="prix" required
                                   pattern="[0-9]{1,8}([.,][0-9]{1,2})?" title="Nombre positif avec 2 décimales au plus, exemple : 149,90"
                                   value="<c:out value='${produit.prix}'/>">
                        </div>
                        <div class="col-6 col-sm-3">
                            <label for="stock" class="form-label">Stock *</label>
                            <input type="number" class="form-control" id="stock" name="stock" required min="0" max="2147483647"
                                   value="${produit.stock}">
                        </div>
                        <div class="col-12">
                            <label for="imageUrl" class="form-label">URL de l'image</label>
                            <input type="url" class="form-control" id="imageUrl" name="imageUrl" maxlength="500"
                                   placeholder="https://…" value="<c:out value='${produit.imageUrl}'/>">
                        </div>
                        <div class="col-12">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="4"
                                      maxlength="2000"><c:out value="${produit.description}"/></textarea>
                        </div>
                        <div class="col-12">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="actif" name="actif"
                                       ${produit.actif ? 'checked' : ''}>
                                <label class="form-check-label" for="actif">
                                    Actif (visible dans le catalogue)
                                </label>
                            </div>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary mt-4">Enregistrer</button>
                    <a class="btn btn-link mt-4" href="${ctx}/admin/produits">Annuler</a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
