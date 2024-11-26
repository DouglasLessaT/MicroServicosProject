import { ToList } from '../models/ToList';
import { ToListService } from '../services/ToListService';

const toListService = new ToListService();

/**
 * Publica uma mensagem de tarefa criada ou atualizada na fila.
 * @param task - Objeto da tarefa que será enviada para a fila.
 */
export const publishToNotificationQueue = async (task: ToList): Promise<void> => {
  try {
    console.log('Enviando mensagem para a fila `notificationQueue`...');
    await toListService.sendToSQS(task, 'notificationQueue');
    console.log('Mensagem publicada com sucesso!');
  } catch (error) {
    console.error('Erro ao publicar mensagem na fila `notificationQueue`:', error);
  }
};
