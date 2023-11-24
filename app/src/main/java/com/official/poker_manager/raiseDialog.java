package com.official.poker_manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.widget.EditText;

/*
Boa parte do código foi extraída de: https://developer.android.com/develop/ui/views/components/dialogs
Principalmente as partes de código com comentários em inglês

Fragmento que exibe um diálogo para o usuário digitar um valor
*/

public class raiseDialog extends DialogFragment {
    
    // ViewModel para passar o valor digitado pelo usuário
    private ValueViewModel viewModel;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater.
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because it's going in the dialog layout.
        builder.setView(inflater.inflate(R.layout.dialog_raise, null))
                // Adiciona um título
                .setTitle(R.string.raise)
                // Add action buttons.
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Pegar o valor digitado pelo usuário
                        viewModel = new ViewModelProvider(requireActivity()).get(ValueViewModel.class);
                        EditText edtxtValue = getDialog().findViewById(R.id.edtxt_input_value);
                        Integer raiseValue = Integer.valueOf(edtxtValue.getText().toString());
                        viewModel.setValue(raiseValue);
                    }
                })
                .setNeutralButton(R.string.all_in, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Retorna flag de all-in
                        viewModel = new ViewModelProvider(requireActivity()).get(ValueViewModel.class);
                        viewModel.setValue(-1);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        raiseDialog.this.getDialog().cancel();
                    }
                });
        
        // Create the AlertDialog object and return it.
        return builder.create();
    }
}