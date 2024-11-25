// toListRespository
import { AppDataSource } from "../data-source";
import { ToList } from "../models/ToList";
import { Repository } from "typeorm";

export class ToListRepository {

    private repository: Repository<ToList>;

    constructor() {
        this.repository = AppDataSource.getRepository(ToList);
    }

    async create(toList: Partial<ToList>): Promise<ToList> {
        const newToList = this.repository.create(toList); // Cria a instância do ToList
        return await this.repository.save(newToList); // Salva no banco de dados
    }

    async findAll(): Promise<ToList[]> {
        return await this.repository.find(); // Busca todos os registros
    }

    async findById(id: string): Promise<ToList | null> {
        return await this.repository.findOneBy({ id }); // Busca um registro pelo ID
    }

    async update(id: string, data: Partial<ToList>): Promise<void> {
        await this.repository.update(id, data); // Atualiza os campos de um registro
    }

    async delete(id: string): Promise<void> {
        await this.repository.delete(id); // Remove um registro pelo ID
    }
}
