import { ToListRepository } from '../repository/ToListRepository';
import { ToList } from '../models/ToList';
import { SQS } from 'aws-sdk';
import { v4 as uuidv4 } from 'uuid'; // Importando a função para gerar UUID

const sqs = new SQS({ region: process.env.AWS_REGION });
const toListRepository = new ToListRepository();
type QueueType = 'apiPrincipalQueue' | 'notificationQueue';

export class ToListService {
  private queueUrls = {
    apiPrincipalQueue: process.env.API_PRINCIPAL_QUEUE_URL!,
    notificationQueue: process.env.NOTIFICATION_QUEUE_URL!,
  };

  async create(item: Partial<ToList>): Promise<ToList> {
    // Verifica se o ID é nulo ou vazio e, se for, gera um UUID
    const id = item.id || uuidv4();
    
    const newToList = await toListRepository.create({
      id: id, 
      description: item.description,
      urlBucketFIle: item.urlBucketFIle,
    });
    
    await this.sendToSQS(newToList, 'notificationQueue');
    return newToList;
  }

  async getAll(): Promise<ToList[]> {
    return toListRepository.findAll();
  }

  async getById(id: string): Promise<ToList | null> {
    return toListRepository.findById(id);
  }

  async update(id: string, updatedItem: Partial<ToList>): Promise<void> {
    await toListRepository.update(id, updatedItem);
    await this.sendToSQS(updatedItem as ToList, 'notificationQueue');
  }

  async delete(id: string): Promise<void> {
    await toListRepository.delete(id);
  }

  public async sendToSQS(toList: ToList, queueType: QueueType): Promise<void> {
    const params = {
      QueueUrl: this.queueUrls[queueType],
      MessageBody: JSON.stringify(toList),
    };

    try {
      const result = await sqs.sendMessage(params).promise();
      console.log(`Mensagem enviada para ${queueType} com sucesso`, result.MessageId);
    } catch (error) {
      console.error(`Erro ao enviar mensagem para ${queueType}`, error);
    }
  }

  async consumeMessagesFromQueue(queueType: QueueType): Promise<void> {
    const queueUrl = this.queueUrls[queueType];
  
    try {
      const messages = await sqs
        .receiveMessage({
          QueueUrl: queueUrl,
          MaxNumberOfMessages: 10,
          WaitTimeSeconds: 20,
        })
        .promise();
  
      if (messages.Messages) {
        for (const message of messages.Messages) {
          const toListData: ToList = JSON.parse(message.Body!);
  
          // Exibe os dados da mensagem no console antes de fazer o create
          console.log('Dados da mensagem recebida:', toListData);
  
          // Se o id for null ou vazio, gera um UUID
          const id = toListData.id || uuidv4();
          
          // Salvar no banco mantendo o UUID da mensagem
          const savedToList = await toListRepository.create({
            id: id,
            description: toListData.description,
            urlBucketFIle: toListData.urlBucketFIle, // Removido typeAction
          });
  
          console.log(`Tarefa salva no banco a partir da fila ${queueType}:`, savedToList);
  
          // Publicar na fila de notificação
          await this.sendToSQS(savedToList, 'notificationQueue');
  
          // Deletar mensagem da fila original
          await sqs
            .deleteMessage({
              QueueUrl: queueUrl,
              ReceiptHandle: message.ReceiptHandle!,
            })
            .promise();
  
          console.log('Mensagem deletada da fila:', message.MessageId);
        }
      } else {
        console.log(`Nenhuma mensagem encontrada na fila ${queueType}.`);
      }
    } catch (error) {
      console.error(`Erro ao consumir mensagens da fila ${queueType}:`, error);
    }
  }
}
