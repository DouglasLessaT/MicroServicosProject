// ToList.ts
import { Entity, PrimaryGeneratedColumn, Column } from "typeorm";

@Entity()
export class ToList {
  @PrimaryGeneratedColumn("uuid")
  id!: string; 

  @Column()
  description!: string;

  @Column({ nullable: true })
  urlBucketAws?: string;
}
