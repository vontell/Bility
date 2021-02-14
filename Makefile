


build-backend:
	./gradlew bility-server:build && \
	cd bility-server && \
	docker build -t vontech/bility-server .

run-backend: 
	docker run --rm --name vontech-bility-server -p 8080:8080 vontech/bility-server:latest 