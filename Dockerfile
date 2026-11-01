FROM eclipse-temurin:21-jre-jammy

LABEL authors="KhaledAdmin"

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libgtk-3-0 libgl1 libxtst6 libxi6 libxrender1 \
        xvfb xauth fonts-noto-core fonts-noto-cjk \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd --gid 10001 app \
    && useradd --uid 10001 --gid app --create-home app

WORKDIR /app

COPY --chown=app:app target/shopping-cart-1.0.0.jar app.jar
COPY --chown=app:app target/lib/ ./lib/

RUN chown app:app /app

USER 10001:10001

ENV LANG=C.UTF-8

ENTRYPOINT ["java", "-Dprism.order=sw", "-cp", "/app/app.jar:/app/lib/*", "edu.assignment.cart.Launcher"]