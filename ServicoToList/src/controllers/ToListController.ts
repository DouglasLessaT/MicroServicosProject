import { Request, Response } from 'express';
import { ToListService } from '../services/ToListService';

const toListService = new ToListService();

export class ToListController {

  create = async (req: Request, res: Response): Promise<Response> => {
    try {
      const { title, status, urlBucketAws } = req.body;
      return res.status(201).json({ message: 'Tarefa criada com sucesso' });
    } catch (error) {
      console.error("Erro ao criar tarefa", error);
      return res.status(500).json({ message: "Erro ao criar tarefa" });
    }
  };

  getAll = async (req: Request, res: Response): Promise<Response> => {
    try {
      const toLists = await toListService.getAll();
      return res.json(toLists);
    } catch (error) {
      console.error("Erro ao buscar tarefas", error);
      return res.status(500).json({ message: "Erro ao buscar tarefas" });
    }
  }

  getById = async (req: Request, res: Response): Promise<Response> => {
    try {
      const { id } = req.params;
      const toList = await toListService.getById(id);
      if (!toList) {
        return res.status(404).json({ message: "Tarefa não encontrada" });
      }
      return res.json(toList);
    } catch (error) {
      console.error("Erro ao buscar tarefa", error);
      return res.status(500).json({ message: "Erro ao buscar tarefa" });
    }
  }

  update =  async (req: Request, res: Response): Promise<Response> => {
    try {
      const { id } = req.params;
      const data = req.body;
      await toListService.update(id, data);
      return res.status(204).send();
    } catch (error) {
      console.error("Erro ao atualizar tarefa", error);
      return res.status(500).json({ message: "Erro ao atualizar tarefa" });
    }
  }
  
  delete = async (req: Request, res: Response): Promise<Response> => {
    try {
      const { id } = req.params;
      await toListService.delete(id);
      return res.status(204).send();
    } catch (error) {
      console.error("Erro ao deletar tarefa", error);
      return res.status(500).json({ message: "Erro ao deletar tarefa" });
    }
  }
  
}
