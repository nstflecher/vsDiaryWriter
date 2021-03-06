package net.viperfish.journal.swtGui;

import net.viperfish.journal.framework.OperationExecutors;
import net.viperfish.journal.operation.OperationFactories;

public class SetPasswordPrompt extends UpdatePasswordPrompt {

	@Override
	protected void doUpdate(String password) {
		OperationExecutors.getExecutor()
				.submit(OperationFactories.getOperationFactory().getSetPasswordOperation(password));
	}

}
