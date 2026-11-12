<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Inscription" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>

<div class="row justify-content-center">
    <div class="col-md-10 col-lg-8">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-4">Créer un compte</h1>
                <form method="post" action="${pageContext.request.contextPath}/inscription">
                    <div class="row g-3">
                        <div class="col-sm-6">
                            <label for="nom" class="form-label">Nom *</label>
                            <input type="text" class="form-control" id="nom" name="nom" required maxlength="100"
                                   value="<c:out value='${saisie.nom}'/>">
                        </div>
                        <div class="col-sm-6">
                            <label for="prenom" class="form-label">Prénom *</label>
                            <input type="text" class="form-control" id="prenom" name="prenom" required maxlength="100"
                                   value="<c:out value='${saisie.prenom}'/>">
                        </div>
                        <div class="col-12">
                            <label for="email" class="form-label">E-mail *</label>
                            <input type="email" class="form-control" id="email" name="email" required maxlength="150"
                                   value="<c:out value='${saisie.email}'/>">
                        </div>
                        <div class="col-sm-6">
                            <label for="motDePasse" class="form-label">Mot de passe * (6 caractères minimum)</label>
                            <input type="password" class="form-control" id="motDePasse" name="motDePasse"
                                   required minlength="6">
                        </div>
                        <div class="col-sm-6">
                            <label for="confirmation" class="form-label">Confirmation du mot de passe *</label>
                            <input type="password" class="form-control" id="confirmation" name="confirmation"
                                   required minlength="6">
                        </div>
                        <div class="col-12">
                            <label for="adresse" class="form-label">Adresse</label>
                            <input type="text" class="form-control" id="adresse" name="adresse" maxlength="255"
                                   value="<c:out value='${saisie.adresse}'/>">
                        </div>
                        <div class="col-sm-6">
                            <label for="telephone" class="form-label">Téléphone</label>
                            <input type="tel" class="form-control" id="telephone" name="telephone" maxlength="20"
                                   value="<c:out value='${saisie.telephone}'/>">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary mt-4">S'inscrire</button>
                </form>
                <p class="mt-3 mb-0 small">
                    Déjà inscrit ? <a href="${pageContext.request.contextPath}/connexion">Se connecter</a>
                </p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
