<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Produits" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h1 class="h3 mb-0">Produits</h1>
    <a class="btn btn-primary" href="${ctx}/admin/produits?action=nouveau">Nouveau produit</a>
</div>

<c:choose>
    <c:when test="${empty produits}">
        <p class="text-muted">Aucun produit pour le moment.</p>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-striped align-middle bg-white">
                <thead>
                <tr>
                    <th>Produit</th><th>Catégorie</th><th class="text-end">Prix</th>
                    <th>Stock (BF19)</th><th>État</th><th class="text-end">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${produits}" var="produit">
                    <tr class="${produit.actif ? '' : 'text-muted'}">
                        <td>
                            <div class="d-flex align-items-center gap-2">
                                <c:if test="${not empty produit.imageUrl}">
                                    <img src="<c:out value='${produit.imageUrl}'/>" alt="" width="40" height="40"
                                         class="rounded object-fit-cover">
                                </c:if>
                                <span class="fw-semibold"><c:out value="${produit.nom}"/></span>
                            </div>
                        </td>
                        <td><c:out value="${produit.categorie.nom}"/></td>
                        <td class="text-end text-nowrap">
                            <fmt:formatNumber value="${produit.prix}" minFractionDigits="2" maxFractionDigits="2"/> DH
                        </td>
                        <td>
                            <form method="post" action="${ctx}/admin/produits" class="d-flex gap-1">
                                <input type="hidden" name="action" value="stock">
                                <input type="hidden" name="id" value="${produit.id}">
                                <input type="hidden" name="page" value="${pageCourante}">
                                <input type="number" name="stock" value="${produit.stock}" min="0" required
                                       class="form-control form-control-sm" style="width: 5.5rem"
                                       aria-label="Stock">
                                <button type="submit" class="btn btn-sm btn-outline-primary">OK</button>
                            </form>
                        </td>
                        <td class="text-nowrap">
                            <c:choose>
                                <c:when test="${produit.actif}"><span class="badge text-bg-success">Actif</span></c:when>
                                <c:otherwise><span class="badge text-bg-secondary">Inactif</span></c:otherwise>
                            </c:choose>
                            <c:if test="${produit.stock == 0}"><span class="badge text-bg-warning">Rupture</span></c:if>
                        </td>
                        <td class="text-end text-nowrap">
                            <a class="btn btn-sm btn-outline-secondary"
                               href="${ctx}/admin/produits?action=modifier&amp;id=${produit.id}">Modifier</a>
                            <form method="post" action="${ctx}/admin/produits" class="d-inline"
                                  onsubmit="return confirm('Supprimer ce produit ? S\'il a déjà été commandé, il sera seulement désactivé.');">
                                <input type="hidden" name="action" value="supprimer">
                                <input type="hidden" name="id" value="${produit.id}">
                                <input type="hidden" name="page" value="${pageCourante}">
                                <button type="submit" class="btn btn-sm btn-outline-danger">Supprimer</button>
                            </form>
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
