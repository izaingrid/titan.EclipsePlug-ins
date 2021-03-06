/******************************************************************************
 * Copyright (c) 2000-2021 Ericsson Telecom AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html
 ******************************************************************************/
package org.eclipse.titan.designer.AST.TTCN3.statements;

import org.eclipse.titan.designer.AST.ASTNode;
import org.eclipse.titan.designer.AST.GovernedSimple.CodeSectionType;
import org.eclipse.titan.designer.AST.ILocateableNode;
import org.eclipse.titan.designer.AST.IValue;
import org.eclipse.titan.designer.AST.Location;
import org.eclipse.titan.designer.AST.NULL_Location;
import org.eclipse.titan.designer.AST.TTCN3.IIncrementallyUpdateable;
import org.eclipse.titan.designer.AST.TTCN3.definitions.Definition;
import org.eclipse.titan.designer.parsers.CompilationTimeStamp;

/**
 * The AltGuard class represents a TTCN3 altstep/alt/interleave branch.
 *
 * @see AltGuards
 *
 * @author Kristof Szabados
 * */
public abstract class AltGuard extends ASTNode implements ILocateableNode, IIncrementallyUpdateable {
	public enum altguard_type {
		AG_OP, AG_REF, AG_INVOKE, AG_ELSE
	}

	public final altguard_type altguardType;

	protected final StatementBlock statementblock;

	/**
	 * The location of the whole altguard. This location encloses the
	 * altguard fully, as it is used to report errors to.
	 **/
	private Location location = NULL_Location.INSTANCE;

	/** the time when this altguard was checked the last time. */
	protected CompilationTimeStamp lastTimeChecked;

	public AltGuard(final altguard_type altguardType, final StatementBlock statementblock) {
		super();
		this.altguardType = altguardType;
		this.statementblock = statementblock;
	}

	public final altguard_type getType() {
		return altguardType;
	}

	public IValue getGuardExpression() {
		return null;
	}

	public final StatementBlock getStatementBlock() {
		return statementblock;
	}

	public abstract void setMyStatementBlock(StatementBlock statementBlock, int index);

	public abstract void setMyDefinition(Definition definition);

	@Override
	/** {@inheritDoc} */
	public final void setLocation(final Location location) {
		this.location = location;
	}

	@Override
	/** {@inheritDoc} */
	public final Location getLocation() {
		return location;
	}

	public abstract void setMyAltguards(AltGuards altGuards);

	/**
	 * Used to tell break and continue statements if they are located with an altstep, a loop or none.
	 *
	 * @param pAltGuards the altguards set only within altguards
	 * @param pLoopStmt the loop statement, set only within loops.
	 * */
	public void setMyLaicStmt(final AltGuards pAltGuards, final Statement pLoopStmt) {
		if (statementblock != null) {
			statementblock.setMyLaicStmt(pAltGuards, pLoopStmt);
		}
	}

	/**
	 * Sets the code_section attribute for the statement to the provided value.
	 *
	 * @param codeSection the code section where this statement should be generated.
	 * */
	public abstract void setCodeSection(final CodeSectionType codeSection);

	/**
	 * Checks whether the altguard has a return statement, either directly
	 * or embedded.
	 *
	 * @param timestamp
	 *                the timestamp of the actual semantic check cycle.
	 *
	 * @return the return status of the altguard.
	 * */
	public abstract StatementBlock.ReturnStatus_type hasReturn(final CompilationTimeStamp timestamp);

	/**
	 * Does the semantic checking of this branch.
	 *
	 * @param timestamp
	 *                the timestamp of the actual semantic check cycle.
	 * */
	public abstract void check(final CompilationTimeStamp timestamp);

	/**
	 * Checks if some statements are allowed in an interleave or not
	 * */
	public abstract void checkAllowedInterleave();

	/**
	 * Checks the properties of the statement, that can only be checked
	 * after the semantic check was completely run.
	 */
	public abstract void postCheck();
}
