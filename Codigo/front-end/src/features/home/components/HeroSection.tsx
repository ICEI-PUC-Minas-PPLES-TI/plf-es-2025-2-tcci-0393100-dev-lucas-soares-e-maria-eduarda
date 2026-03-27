import { Plus, Upload } from 'lucide-react';
import { motion } from 'motion/react';
import { GraphBackground } from '../../../components/GraphBackground';
import { Button } from '../../../components/Button';

export function HeroSection() {
  return (
    <section className="relative overflow-hidden border-b border-gray-800">
      <GraphBackground className="absolute inset-0 w-full h-full" nodeCount={30} />

      <div className="container mx-auto px-6 py-20 relative z-10">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="max-w-4xl mx-auto text-center"
        >
          <h2 className="text-gray-100 text-3xl font-bold mb-4">
            Análise Estrutural e Funcional de Código Java
          </h2>
          <p className="text-gray-400 mb-8 max-w-2xl mx-auto">
            Gere grafos, derive tabelas de decisão a partir de trechos de código.
          </p>

          <div className="flex flex-wrap gap-4 justify-center">
            <Button className="px-6 py-3">
              <Plus className="w-5 h-5" />
              Criar novo projeto
            </Button>
            <Button variant="secondary" className="px-6 py-3">
              <Upload className="w-5 h-5" />
              Importar código
            </Button>
          </div>
        </motion.div>
      </div>
    </section>
  );
}
