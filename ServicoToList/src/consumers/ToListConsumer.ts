import { ToListService } from '../services/ToListService';

const toListService = new ToListService();

export const startToListConsumer = async (): Promise<void> => {
  console.log('Iniciando o consumidor de mensagens da fila `apiPrincipalQueue`...');
  
  try {
    while (true) { // Loop infinito para continuar consumindo mensagens
      await toListService.consumeMessagesFromQueue('apiPrincipalQueue');
    }
  } catch (error) {
    console.error('Erro no consumidor da fila `apiPrincipalQueue`:', error);
  }
};
