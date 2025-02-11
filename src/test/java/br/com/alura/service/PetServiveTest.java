package br.com.alura.service;

import br.com.alura.client.ClientHttpConfiguration;
import br.com.alura.domain.Pet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.http.HttpResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PetServiveTest {

    private final ClientHttpConfiguration client = mock(ClientHttpConfiguration.class);
    private final PetService petService = new PetService(client);
    private final HttpResponse<String> response = mock(HttpResponse.class);

    @Test
    public void deveListarPetsQuandoExistemPets() throws IOException, InterruptedException {
        String jsonResponse = "[{\"id\":1,\"tipo\":\"Cachorro\",\"nome\":\"Rex\",\"raca\":\"Labrador\",\"idade\":5,\"cor\":\"Amarelo\",\"peso\":30.0}]";
        when(response.body()).thenReturn(jsonResponse);
        when(client.dispararRequisicaoGet(anyString())).thenReturn(response);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        petService.listarPets();

        String[] output = baos.toString().split(System.lineSeparator());
        Assertions.assertEquals("Pets cadastrados:", output[0]);
        Assertions.assertEquals("1 - Cachorro - Rex - Labrador", output[1]);
    }

    @Test
    public void deveMostrarMensagemQuandoNaoExistemPets() throws IOException, InterruptedException {
        when(response.body()).thenReturn("[]");
        when(client.dispararRequisicaoGet(anyString())).thenReturn(response);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        petService.listarPets();

        String[] output = baos.toString().split(System.lineSeparator());
        Assertions.assertEquals("Pets cadastrados:", output[0]);
    }

    @Test
    public void deveImportarPetsComSucesso() throws IOException, InterruptedException {
        String csvContent = "Cachorro,Rex,Labrador,5,Amarelo,30.0\n";
        ByteArrayInputStream csvInputStream = new ByteArrayInputStream(csvContent.getBytes());
        System.setIn(csvInputStream);

        String input = "1\npets.csv\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(client.dispararRequisicaoPost(anyString(), any(Pet.class))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("Pet cadastrado com sucesso");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        petService.importarPets();

        String[] output = baos.toString().split(System.lineSeparator());
        Assertions.assertEquals("Pet cadastrado com sucesso: Rex", output[0]);
    }

    @Test
    public void deveMostrarErroQuandoArquivoNaoEncontrado() throws IOException, InterruptedException {
        String input = "1\narquivo_inexistente.csv\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        petService.importarPets();

        String[] output = baos.toString().split(System.lineSeparator());
        Assertions.assertEquals("Erro ao carregar o arquivo: arquivo_inexistente.csv", output[0]);
    }


}
