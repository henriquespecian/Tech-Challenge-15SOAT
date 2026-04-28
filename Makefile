.PHONY: start up down build restart logs logs-db ps clean info

APP_PORT  = 8080
DB_PORT   = 5432

info:
	@echo ""
	@echo "  Aplicacao : http://localhost:$(APP_PORT)"
	@echo "  Swagger   : http://localhost:$(APP_PORT)/swagger-ui.html"
	@echo "  Banco     : localhost:$(DB_PORT)  (oficina_db / postgres)"
	@echo ""

start:
	docker-compose down -v --rmi local
	@$(MAKE) info
	docker-compose up --build
