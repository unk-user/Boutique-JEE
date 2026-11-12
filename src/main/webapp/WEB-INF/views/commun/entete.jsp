<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty titre ? 'Boutique en ligne' : titre}"/> · Boutique en ligne</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
<nav class="navbar navbar-expand-md navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${ctx}/catalogue">Boutique en ligne</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#menu"
                aria-controls="menu" aria-expanded="false" aria-label="Afficher le menu">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="menu">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="${ctx}/catalogue">Catalogue</a></li>
                <c:if test="${sessionScope.utilisateur.role == 'ADMIN'}">
                    <li class="nav-item"><a class="nav-link" href="${ctx}/admin/tableau-de-bord">Tableau de bord</a></li>
                    <li class="nav-item"><a class="nav-link" href="${ctx}/admin/categories">Catégories</a></li>
                    <li class="nav-item"><a class="nav-link" href="${ctx}/admin/produits">Produits</a></li>
                    <li class="nav-item"><a class="nav-link" href="${ctx}/admin/commandes">Commandes</a></li>
                </c:if>
                <c:if test="${sessionScope.utilisateur.role == 'CLIENT'}">
                    <li class="nav-item"><a class="nav-link" href="${ctx}/client/commandes">Mes commandes</a></li>
                </c:if>
            </ul>
            <c:choose>
                <c:when test="${empty sessionScope.utilisateur}">
                    <a class="btn btn-outline-light btn-sm me-2" href="${ctx}/connexion">Connexion</a>
                    <a class="btn btn-light btn-sm" href="${ctx}/inscription">Inscription</a>
                </c:when>
                <c:otherwise>
                    <c:if test="${sessionScope.utilisateur.role == 'CLIENT'}">
                        <a class="btn btn-outline-light btn-sm me-2" href="${ctx}/client/panier">
                            Panier
                            <span class="badge text-bg-light">
                                <c:out value="${empty sessionScope.panier ? 0 : sessionScope.panier.lignes.size()}"/>
                            </span>
                        </a>
                    </c:if>
                    <span class="navbar-text me-3">
                        <c:out value="${sessionScope.utilisateur.prenom} ${sessionScope.utilisateur.nom}"/>
                    </span>
                    <form method="post" action="${ctx}/deconnexion" class="d-inline">
                        <button type="submit" class="btn btn-outline-light btn-sm">Déconnexion</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>
<main class="container py-4 flex-grow-1">
    <c:if test="${not empty succes}">
        <div class="alert alert-success" role="alert"><c:out value="${succes}"/></div>
    </c:if>
    <c:if test="${not empty erreur}">
        <div class="alert alert-danger" role="alert"><c:out value="${erreur}"/></div>
    </c:if>
    <c:if test="${not empty sessionScope.flashSucces}">
        <div class="alert alert-success" role="alert"><c:out value="${sessionScope.flashSucces}"/></div>
        <c:remove var="flashSucces" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.flashErreur}">
        <div class="alert alert-danger" role="alert"><c:out value="${sessionScope.flashErreur}"/></div>
        <c:remove var="flashErreur" scope="session"/>
    </c:if>
