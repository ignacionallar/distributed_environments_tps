import random
from locust import HttpUser, task, between, events

class UsuarioDistribuido(HttpUser):
    # Simula un tiempo de espera aleatorio entre 1 y 2.5 segundos entre peticiones
    wait_time = between(1.0, 2.5)

    @task(5)
    def obtener_usuarios(self):
        """Petición de lectura ligera (Peso 5)"""
        with self.client.get("/api/usuarios", catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Fallo en lectura: Status {response.status_code}")

    @task(2)
    def crear_usuario(self):
        """Petición de escritura (Peso 2)"""
        user_id = random.randint(1000, 9999)
        payload = {
            "nombre": f"Usuario_Prueba_{user_id}",
            "email": f"loadtest_{user_id}@distribuidas.com"
        }
        headers = {"Content-Type": "application/json"}
        
        with self.client.post("/api/usuarios", json=payload, headers=headers, catch_response=True) as response:
            if response.status_code == 201:
                response.success()
            else:
                response.failure(f"Fallo en creación: Status {response.status_code}")

    @task(1)
    def procesar_reporte_pesado(self):
        """Petición intensiva en CPU (Peso 1 - Prueba de Estrés)"""
        with self.client.get("/api/procesar-reporte", catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Saturación de servidor: Status {response.status_code}")