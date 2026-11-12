<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titre" value="Valider ma commande" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<h1 class="h3 mb-3">Valider ma commande</h1>

<c:choose>
    <c:when test="${empty panier.lignes}">
        <div class="alert alert-info" role="alert">Votre panier est vide.</div>
        <a class="btn btn-primary" href="${ctx}/catalogue">Continuer mes achats</a>
    </c:when>
    <c:otherwise>
        <div class="row g-4">
            <div class="col-lg-7">
                <h2 class="h5">Récapitulatif</h2>
                <div class="table-responsive bg-white rounded shadow-sm">
                    <table class="table align-middle mb-0">
                        <thead>
                            <tr>
                                <th>Produit</th>
                                <th class="text-end">Prix unitaire</th>
                                <th class="text-end">Quantité</th>
                                <th class="text-end">Sous-total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${panier.lignes}" var="ligne">
                                <tr>
                                    <td><c:out value="${ligne.produit.nom}"/></td>
                                    <td class="text-end text-nowrap">
                                        <fmt:formatNumber value="${ligne.produit.prix}" minFractionDigits="2" maxFractionDigits="2"/> DH
                                    </td>
                                    <td class="text-end"><c:out value="${ligne.quantite}"/></td>
                                    <td class="text-end text-nowrap">
                                        <fmt:formatNumber value="${ligne.sousTotal}" minFractionDigits="2" maxFractionDigits="2"/> DH
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
                            </tr>
                        </tfoot>
                    </table>
                </div>
                <p class="small text-muted mt-2 mb-0">
                    Les prix et le stock sont vérifiés à la confirmation : le montant enregistré est celui du jour.
                </p>
            </div>
            <div class="col-lg-5">
                <div class="card shadow-sm">
                    <div class="card-body p-4">
                        <h2 class="h5 mb-3">Livraison</h2>
                        <form method="post" action="${ctx}/client/commandes">
                            <div class="mb-3">
                                <label for="adresseLivraison" class="form-label">Adresse de livraison *</label>
                                <textarea class="form-control" id="adresseLivraison" name="adresseLivraison" rows="3"
                                          required maxlength="255"><c:out value="${adresseLivraison}"/></textarea>
                            </div>
                            <p class="small text-muted">Paiement à la livraison.</p>
                            <div class="d-flex flex-wrap gap-2">
                                <a class="btn btn-outline-secondary" href="${ctx}/client/panier">Retour au panier</a>
                                <button type="submit" class="btn btn-success ms-auto">Confirmer la commande</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
