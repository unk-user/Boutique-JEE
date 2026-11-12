<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${nbPages > 1}">
    <c:set var="cheminServlet" value="${requestScope['jakarta.servlet.forward.servlet_path']}"/>
    <nav aria-label="Pagination">
        <ul class="pagination justify-content-center flex-wrap">
            <c:forEach begin="0" end="${nbPages + 1}" var="i">
                <c:choose>
                    <c:when test="${i == 0}">
                        <c:set var="cible" value="${pageCourante - 1}"/>
                        <c:set var="libelle" value="Précédent"/>
                        <c:set var="inactif" value="${pageCourante <= 1}"/>
                    </c:when>
                    <c:when test="${i == nbPages + 1}">
                        <c:set var="cible" value="${pageCourante + 1}"/>
                        <c:set var="libelle" value="Suivant"/>
                        <c:set var="inactif" value="${pageCourante >= nbPages}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="cible" value="${i}"/>
                        <c:set var="libelle" value="${i}"/>
                        <c:set var="inactif" value="false"/>
                    </c:otherwise>
                </c:choose>
                <c:url var="lien" value="${cheminServlet}">
                    <c:forEach items="${paramValues}" var="p">
                        <c:if test="${p.key != 'page'}">
                            <c:forEach items="${p.value}" var="v"><c:param name="${p.key}" value="${v}"/></c:forEach>
                        </c:if>
                    </c:forEach>
                    <c:param name="page" value="${cible}"/>
                </c:url>
                <li class="page-item${inactif ? ' disabled' : ''}${i == pageCourante && i != 0 && i != nbPages + 1 ? ' active' : ''}">
                    <a class="page-link" href="${lien}"><c:out value="${libelle}"/></a>
                </li>
            </c:forEach>
        </ul>
    </nav>
</c:if>
