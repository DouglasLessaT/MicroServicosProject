// ToList.ts
import { Entity, PrimaryGeneratedColumn, Column } from "typeorm";

@Entity()
export class ToList {
  @PrimaryGeneratedColumn("uuid")
  id!: string; 
  @Column()
  title!: string;

  @Column()
  status: string = "pending";

  @Column({ nullable: true })
  urlBucketAws?: string;
}
