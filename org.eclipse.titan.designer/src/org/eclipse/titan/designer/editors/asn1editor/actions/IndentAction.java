/******************************************************************************
 * Copyright (c) 2000-2021 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 ******************************************************************************/
package org.eclipse.titan.designer.editors.asn1editor.actions;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.RewriteSessionEditProcessor;
import org.eclipse.titan.common.parsers.Interval;
import org.eclipse.titan.common.parsers.Interval.interval_type;
import org.eclipse.titan.designer.editors.actions.AbstractIndentAction;
import org.eclipse.titan.designer.editors.asn1editor.ASN1Editor;
import org.eclipse.ui.IEditorPart;

/**
 * @author Kristof Szabados
 * */
public final class IndentAction extends AbstractIndentAction {

	@Override
	protected IDocument getDocument() {
		final IEditorPart editorPart = getTargetEditor();
		if (editorPart instanceof ASN1Editor) {
			return ((ASN1Editor) editorPart).getDocument();
		}
		return null;
	}

	@Override
	protected int lineIndentationLevel(final IDocument document, final int realStartOffset, final int lineEndOffset,
			final Interval startEnclosingInterval) throws BadLocationException {
		if (realStartOffset + 1 == lineEndOffset) {
			return 0;
		}

		if (interval_type.MULTILINE_COMMENT.equals(startEnclosingInterval.getType())
				|| startEnclosingInterval.getStartOffset() == realStartOffset
				|| interval_type.SINGLELINE_COMMENT.equals(startEnclosingInterval.getType())) {
			// indent comments according to outer interval
			return Math.max(0, startEnclosingInterval.getDepth() - 1);
		}

		if (startEnclosingInterval.getEndOffset() < lineEndOffset
				&& !containsNonWhiteSpace(document.get(realStartOffset,
						Math.max(startEnclosingInterval.getEndOffset() - realStartOffset - 1, 0)))) {
			// indent lines containing closing bracket according to
			// the line with the opening bracket.
			return Math.max(0, startEnclosingInterval.getDepth() - 1);
		}

		return startEnclosingInterval.getDepth();
	}

	@Override
	protected void performEdits(final RewriteSessionEditProcessor processor) throws BadLocationException {
		processor.performEdits();
	}
}
