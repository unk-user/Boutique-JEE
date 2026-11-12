<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titre" value="Connexion" scope="request"/>
<jsp:include page="/WEB-INF/views/commun/entete.jsp"/>

<div class="row justify-content-center">
    <div class="col-sm-10 col-md-6 col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-4">Connexion</h1>
                <form method="post" action="${pageContext.request.contextPath}/connexion">
                    <div class="mb-3">
                        <label for="email" class="form-label">E-mail</label>
                        <input type="email" class="form-control" id="email" name="email" required maxlength="150"
                               autocomplete="username" value="<c:out value='${email}'/>">
                    </div>
                    <div class="mb-3">
                        <label for="motDePasse" class="form-label">Mot de passe</label>
                        <input type="password" class="form-control" id="motDePasse" name="motDePasse" required
                               autocomplete="current-password">
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Se connecter</button>
                </form>
                <p class="mt-3 mb-0 small text-center">
                    Pas encore de compte ?
                    <a href="${pageContext.request.contextPath}/inscription">Créer un compte</a>
                </p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/commun/pied.jsp"/>
