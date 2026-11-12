<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Mon panier" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<h1 class="h3 mb-3">Mon panier</h1>

<c:choose>
    <c:when test="${empty panier.lignes}">
        <div class="alert alert-info" role="alert">Votre panier est vide.</div>
        <a class="btn btn-primary" href="${ctx}/catalogue">Continuer mes achats</a>
    </c:when>
    <c:otherwise>
        <div class="table-responsive bg-white rounded shadow-sm">
            <table class="table align-middle mb-0">
                <thead>
                    <tr>
                        <th>Produit</th>
                        <th class="text-end">Prix unitaire</th>
                        <th>Quantité</th>
                        <th class="text-end">Sous-total</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${panier.lignes}" var="ligne">
                        <tr>
                            <td>
                                <a href="${ctx}/produit?id=${ligne.produit.id}"><c:out value="${ligne.produit.nom}"/></a>
                            </td>
                            <td class="text-end text-nowrap">
                                <fmt:formatNumber value="${ligne.produit.prix}" minFractionDigits="2" maxFractionDigits="2"/> DH
                            </td>
                            <td>
                                <form method="post" action="${ctx}/client/panier" class="d-flex gap-2">
                                    <input type="hidden" name="action" value="modifier">
                                    <input type="hidden" name="produitId" value="${ligne.produit.id}">
                                    <input type="number" name="quantite" value="${ligne.quantite}" min="1"
                                           max="${ligne.produit.stock}" required class="form-control form-control-sm"
                                           style="width: 5.5rem" aria-label="Quantité de <c:out value='${ligne.produit.nom}'/>">
                                    <button type="submit" class="btn btn-outline-secondary btn-sm">Mettre à jour</button>
                                </form>
                            </td>
                            <td class="text-end text-nowrap">
                                <fmt:formatNumber value="${ligne.sousTotal}" minFractionDigits="2" maxFractionDigits="2"/> DH
                            </td>
                            <td class="text-end">
                                <form method="post" action="${ctx}/client/panier">
                                    <input type="hidden" name="action" value="retirer">
                                    <input type="hidden" name="produitId" value="${ligne.produit.id}">
                                    <button type="submit" class="btn btn-outline-danger btn-sm">Retirer</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <th colspan="3" class="text-end">Total</th>
                        <th class="text-end text-nowrap">
                            <fmt:formatNumber value="${panier.total}" minFractionDigits="2" maxFractionDigits="2"/> DH
                        </th>
                        <th></th>
                    </tr>
                </tfoot>
            </table>
        </div>

        <div class="d-flex flex-wrap gap-2 mt-3">
            <a class="btn btn-outline-primary" href="${ctx}/catalogue">Continuer mes achats</a>
            <form method="post" action="${ctx}/client/panier">
                <input type="hidden" name="action" value="vider">
                <button type="submit" class="btn btn-outline-danger">Vider le panier</button>
            </form>
            <a class="btn btn-success ms-md-auto" href="${ctx}/client/commandes?action=valider">Passer la commande</a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
