package br.com.lisboa.ikigai;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.SeekBar;

import static java.lang.Math.abs;

public class ColorDialogFragment extends DialogFragment {
    //seekbar para alpha
    private SeekBar alphaSeekBar;
    //seekbar para red
    private SeekBar redSeekBar;
    //seekbar para green
    private SeekBar greenSeekBar;
    //seekbar para blue
    private SeekBar blueSeekBar;
    //campo de amostra de cor
    private View colorView;
    //cor atual
    private int color;

    private
    MainActivityFragment getDoodleFragment(){
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.doodleFragment);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//builder para o diálogo
        AlertDialog.Builder builder =
                new
                        AlertDialog.Builder(getActivity());
//infla a view do fragment
        View colorDialogView =
                getActivity().
                        getLayoutInflater().
                        inflate(R.layout.
                                        fragment_color
                                ,
                                null
                        );
        builder.setView(colorDialogView);
        builder.setTitle(R.string.
                title_color_dialog
        );
//busca os componentes visuais na árvore
        alphaSeekBar =
                colorDialogView.findViewById(R.id.alphaSeekBar);
        redSeekBar
                =
                colorDialogView.findViewById(R.id.redSeekBar);
        greenSeekBar
                =
                colorDialogView.findViewById(R.id.greenSeekBar);
        blueSeekBar
                = colorDialogView.findViewById(R.id.blueSeekBar);
        colorView
                = colorDialogView.findViewById(R.id.colorView);
//registra o observador em todas as SeekBars
        alphaSeekBar.setOnSeekBarChangeListener(
                colorChangedListener);
        redSeekBar.setOnSeekBarChangeListener(
                colorChangedListener);
        greenSeekBar.setOnSeekBarChangeListener(
                        colorChangedListener);
        blueSeekBar.setOnSeekBarChangeListener(
                colorChangedListener);
//pega o DoodleView para pegar a cor atual e configurar
//o progresso das barras
        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        color = doodleView.getDrawingColor();
//configura o progresso de cada barra
        redSeekBar.setProgress(Color.red(color));
        color = doodleView.getDrawingColor();
        greenSeekBar.setProgress(Color.green(color));
        color = doodleView.getDrawingColor();
        blueSeekBar.setProgress(Color.blue(color));
        color = doodleView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
//configura um botão com o texto e o observador
        builder.setPositiveButton(
                R.string.button_set_color, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doodleView.setDrawingColor(color);
                            }
                        });
//devolve o dialogo construído pelo builder
        return builder.create();
    }

    private final
    SeekBar.OnSeekBarChangeListener
            colorChangedListener = new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void
                        onProgressChanged(SeekBar seekBar,
                                          int progress,
                                          boolean fromUser) {
                            color = Color.argb
                                            (
                                                    alphaSeekBar.getProgress(),
                                                    redSeekBar.getProgress(),
                                                    greenSeekBar.getProgress(),
                                                    blueSeekBar.getProgress()
                                            );
                            colorView.setBackgroundColor(color);
                        }
                        @Override
                        public void
                        onStartTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    };

    @Override
    public void
    onAttach(Context context) {
        super.onAttach(context);
        MainActivityFragment fragment = getDoodleFragment();
        if
        (fragment != null){
//o diálogo está sendo exibido
            fragment.setDialogOnScreen(true);
        }
    }
    @Override
    public void
    onDetach() { super.onDetach();
        MainActivityFragment fragment = getDoodleFragment();
        if (fragment != null)
//o diálogo não está mais na tela
            fragment.setDialogOnScreen(false);
    }
}
