import { ToListRepository } from '../repository/ToListRepository';
import { ToList } from '../models/ToList';
import { SQS } from 'aws-sdk';

const sqs = new SQS({ region: 'us-east-1' });
const toListRepository = new ToListRepository();

export class ToListService {
  // URLs das filas SQS
  private queueUrls = {
    apiPrincipalQueue: 'https://sqs.us-east-1.amazonaws.com/your-account-id/api-principal-queue',
    notificationQueue: 'https://sqs.us-east-1.amazonaws.com/your-account-id/notification-queue'
  };


  async create(item: Partial<ToList>): Promise<ToList> {
    const newToList = await toListRepository.create(item);
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

  private async sendToSQS(toList: ToList, queueType: 'notificationQueue'): Promise<void> {
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


  async consumeMessagesFromQueue(queueType: 'apiPrincipalQueue'): Promise<void> {
    const queueUrl = this.queueUrls[queueType];
    try {
      const messages = await sqs.receiveMessage({
        QueueUrl: queueUrl,
        MaxNumberOfMessages: 10,  
        WaitTimeSeconds: 20,  
      }).promise();

      if (messages.Messages) {
        for (const message of messages.Messages) {
          const toListData: ToList = JSON.parse(message.Body!); 
          
          await toListRepository.create(toListData);

          console.log(`Tarefa salva no banco a partir da fila ${queueType}:`, toListData);


          await sqs.deleteMessage({
            QueueUrl: queueUrl,
            ReceiptHandle: message.ReceiptHandle!,
          }).promise();
          
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
