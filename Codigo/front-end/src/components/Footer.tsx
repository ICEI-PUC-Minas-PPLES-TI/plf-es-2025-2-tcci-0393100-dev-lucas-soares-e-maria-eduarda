import { BookOpen, HelpCircle, Github } from 'lucide-react';

export function Footer() {
  return (
    <footer className="border-t border-edge bg-surface mt-16">
      <div className="container mx-auto px-6 py-8">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="text-sm text-gray-400">
            <p>© 2025 GraphTest. Todos os direitos reservados.</p>
            <p className="text-xs text-gray-500 mt-1">Versão 1.0.0</p>
          </div>

          <div className="flex gap-6">
            <a
              href="#"
              className="flex items-center gap-2 text-sm text-gray-400 hover:text-primary-light transition-colors"
            >
              <BookOpen className="w-4 h-4" />
              Documentação
            </a>
            <a
              href="#"
              className="flex items-center gap-2 text-sm text-gray-400 hover:text-primary-light transition-colors"
            >
              <HelpCircle className="w-4 h-4" />
              Suporte
            </a>
            <a
              href="#"
              className="flex items-center gap-2 text-sm text-gray-400 hover:text-primary-light transition-colors"
            >
              <Github className="w-4 h-4" />
              Repositório
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
