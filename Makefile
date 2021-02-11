


build-backend:
	./gradlew bility-server:build && \
	cd bility-server && \
	docker build -t vontech/bility-server .

run-backend: 
	docker run --rm --name vontech-bility-server vontech/bility-server:latest