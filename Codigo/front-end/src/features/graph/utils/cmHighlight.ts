import { EditorView, Decoration, type DecorationSet } from '@codemirror/view';
import { StateField, StateEffect, RangeSetBuilder } from '@codemirror/state';

export interface HighlightRange {
  startLine: number;
  endLine: number;
}

export const setHighlightEffect = StateEffect.define<HighlightRange | null>();

/**
 * Campo CodeMirror que mantém um `DecorationSet` de linhas destacadas, atualizado
 * via `setHighlightEffect`. Use `EditorView.scrollIntoView` para rolar até a linha.
 */
export const highlightField = StateField.define<DecorationSet>({
  create: () => Decoration.none,
  update(deco, tr) {
    let next = deco.map(tr.changes);
    for (const eff of tr.effects) {
      if (eff.is(setHighlightEffect)) {
        if (eff.value == null) {
          next = Decoration.none;
          continue;
        }
        const builder = new RangeSetBuilder<Decoration>();
        const doc = tr.state.doc;
        const start = Math.max(1, eff.value.startLine);
        const end = Math.min(doc.lines, Math.max(start, eff.value.endLine));
        for (let l = start; l <= end; l++) {
          const line = doc.line(l);
          builder.add(line.from, line.from, Decoration.line({ class: 'gfc-highlight-line' }));
        }
        next = builder.finish();
      }
    }
    return next;
  },
  provide: (f) => EditorView.decorations.from(f),
});
