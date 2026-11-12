<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<div class="row g-4">
    <div class="col-lg-4 order-lg-2">
        <div class="card shadow-sm">
            <div class="card-body">
                <dl class="mb-0">
                    <dt>Date</dt>
                    <dd><c:out value="${commande.dateCommandeFormatee}"/></dd>
                    <dt>Statut</dt>
                    <dd><jsp:include page="/WEB-INF/views/commun/statut.jsp"/></dd>
                    <dt>Adresse de livraison</dt>
                    <dd class="mb-0" style="white-space: pre-line"><c:out value="${commande.adresseLivraison}"/></dd>
                </dl>
            </div>
        </div>
    </div>
    <div class="col-lg-8 order-lg-1">
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
                    <c:forEach items="${commande.lignes}" var="ligne">
                        <tr>
                            <td><c:out value="${ligne.produit.nom}"/></td>
                            <td class="text-end text-nowrap">
                                <fmt:formatNumber value="${ligne.prixUnitaire}" minFractionDigits="2" maxFractionDigits="2"/> DH
                            </td>
                            <td class="text-end"><c:out value="${ligne.quantite}"/></td>
                            <td class="text-end text-nowrap">
                                <fmt:formatNumber value="${ligne.prixUnitaire * ligne.quantite}" minFractionDigits="2" maxFractionDigits="2"/> DH
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <th colspan="3" class="text-end">Total</th>
                        <th class="text-end text-nowrap">
                            <fmt:formatNumber value="${commande.montantTotal}" minFractionDigits="2" maxFractionDigits="2"/> DH
                        </th>
                    </tr>
                </tfoot>
            </table>
        </div>
    </div>
</div>
