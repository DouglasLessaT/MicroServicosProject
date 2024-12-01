import dotenv from 'dotenv';
dotenv.config();

import { DataSource } from "typeorm";
import { ToList } from '../models/ToList';

export const AppDataSource = new DataSource({
    type: "mysql",
    host: process.env.MYSQL_HOST || 'localhost',
    port: parseInt(process.env.MYSQL_PORT || '3306', 10),
    username: process.env.MYSQL_USER || 'root',
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE || 'toListBd',
    synchronize: true,
    logging: true,
    entities:[ToList],
});

