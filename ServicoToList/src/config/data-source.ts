import { DataSource } from "typeorm";

export const AppDataSource = new DataSource({
    type: "mysql",
    host: "localhost", // Substitua pelo host do seu banco de dados
    port: 3306,        // Porta padrão do MySQL
    username: "root",  // Seu usuário do MySQL
    password: "senha123", // Sua senha do MySQL
    database: "todo_db", // Nome do banco de dados
    synchronize: true,  // Sincroniza o schema automaticamente (não recomendado em produção)
    logging: true,      // Mostra logs das consultas no console
    entities: [__dirname + "/models/*.ts"], // Indique onde estão suas entidades
});
