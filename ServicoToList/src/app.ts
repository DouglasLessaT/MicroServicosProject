import express, { Application } from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';
import { AppDataSource } from './config/DataSource';
import { startToListConsumer } from './consumers/ToListConsumer';
import { ToListService } from './services/ToListService';
import { publishToNotificationQueue } from './publishers/ToListPublisher';
import dotenv from 'dotenv';

dotenv.config();
// Inicializa o serviço e a conexão do banco
const app: Application = express();
AppDataSource.initialize()
  .then(() => console.log('Banco de dados conectado com sucesso!'))
  .catch((error) => console.error('Erro ao conectar ao banco de dados:', error));

// Middlewares
app.use(cors());
app.use(bodyParser.json());

// Inicializa o consumidor
startToListConsumer();

// Exemplo de publicação usando o publisher
const toListService = new ToListService();

// Criar um novo item e enviar para a fila
app.post('/todo', async (req, res) => {
  const { id, description, urlBucketFIle } = req.body;  // Agora espera o UUID

  try {
    const newTask = await toListService.create({ id, description, urlBucketFIle }); // Passa o UUID
    await publishToNotificationQueue(newTask); // Publica na fila
    res.status(201).json({ message: 'Tarefa criada com sucesso!', data: newTask });
  } catch (error) {
    console.error('Erro ao criar tarefa:', error);
    res.status(500).json({ error: 'Erro ao criar a tarefa' });
  }
});

// Inicia o servidor
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`Servidor rodando na porta ${PORT}`);
});
