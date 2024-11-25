import express, { Application } from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';

const app: Application = express();

app.use(cors());
app.use(bodyParser.json());


const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
