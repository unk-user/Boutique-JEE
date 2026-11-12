<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Mon profil" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="p" value="${empty saisie ? sessionScope.utilisateur : saisie}"/>

<h1 class="h3 mb-3">Mon profil</h1>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h2 class="h5 mb-3">Mes informations</h2>
                <form method="post" action="${ctx}/client/profil">
                    <div class="row g-3">
                        <div class="col-sm-6">
                            <label for="nom" class="form-label">Nom *</label>
                            <input type="text" class="form-control" id="nom" name="nom" required maxlength="100"
                                   value="<c:out value='${p.nom}'/>">
                        </div>
                        <div class="col-sm-6">
                            <label for="prenom" class="form-label">Prénom *</label>
                            <input type="text" class="form-control" id="prenom" name="prenom" required maxlength="100"
                                   value="<c:out value='${p.prenom}'/>">
                        </div>
                        <div class="col-12">
                            <label for="email" class="form-label">E-mail *</label>
                            <input type="email" class="form-control" id="email" name="email" required maxlength="150"
                                   value="<c:out value='${p.email}'/>">
                        </div>
                        <div class="col-12">
                            <label for="adresse" class="form-label">Adresse</label>
                            <input type="text" class="form-control" id="adresse" name="adresse" maxlength="255"
                                   value="<c:out value='${p.adresse}'/>">
                        </div>
                        <div class="col-sm-6">
                            <label for="telephone" class="form-label">Téléphone</label>
                            <input type="tel" class="form-control" id="telephone" name="telephone" maxlength="20"
                                   value="<c:out value='${p.telephone}'/>">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary mt-4">Enregistrer</button>
                </form>
            </div>
        </div>
    </div>

    <div class="col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h2 class="h5 mb-3">Changer mon mot de passe</h2>
                <form method="post" action="${ctx}/client/profil">
                    <input type="hidden" name="action" value="motdepasse">
                    <div class="mb-3">
                        <label for="motDePasseActuel" class="form-label">Mot de passe actuel *</label>
                        <input type="password" class="form-control" id="motDePasseActuel" name="motDePasseActuel"
                               required autocomplete="current-password">
                    </div>
                    <div class="mb-3">
                        <label for="nouveauMotDePasse" class="form-label">Nouveau mot de passe * (6 caractères minimum)</label>
                        <input type="password" class="form-control" id="nouveauMotDePasse" name="nouveauMotDePasse"
                               required minlength="6" autocomplete="new-password">
                    </div>
                    <div class="mb-3">
                        <label for="confirmation" class="form-label">Confirmation du nouveau mot de passe *</label>
                        <input type="password" class="form-control" id="confirmation" name="confirmation"
                               required minlength="6" autocomplete="new-password">
                    </div>
                    <button type="submit" class="btn btn-outline-primary">Modifier le mot de passe</button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
