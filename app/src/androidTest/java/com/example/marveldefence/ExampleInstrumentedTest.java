package com.example.marveldefence;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Teste instrumentado, executado em um aparelho Android.
 *
 * @see <a href="http://d.android.com/tools/testing">Documentação de testes</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Contexto do aplicativo em teste.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.marveldefence", appContext.getPackageName());
    }
}
