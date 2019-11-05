package com.example.cadastroproduto.service;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import ddm.com.br.ddm_consulta_produto.MyApp;
//import ddm.com.br.ddm_consulta_produto.R;
import com.example.cadastroproduto.model.Produto;
import com.example.cadastroproduto.utils.ConfigSharedPreferences;
import com.example.cadastroproduto.utils.DateUtil;
import com.example.cadastroproduto.utils.HttpHelper;
import com.example.cadastroproduto.utils.NetworkType;


public class ProdutoService {

    public static List<Produto> getProdutos(boolean isForcarAtualizacao) throws IOException {
        List<Produto> produtos = null;
        boolean bGet = false;
        String json = "";
        String mensagem2 = "";
        String networkType = NetworkType.getNetworkClass(MyApp.getContext());

        String sServidorIP = ConfigSharedPreferences.getString(MyApp.getContext(), "cfgServidorIP");
        // Log.w("DDM - Log Sandro", "sServidorIP = " + sServidorIP);

        if (networkType.equals("-") || !networkType.equals("WIFI")) {
            String mensagem = (networkType.equals("-") ? "Sem conexão com a Internet." : "Conexão " + networkType);

            if (sServidorIP != null && !sServidorIP.isEmpty())
                mensagem2 = MyApp.getContext().getResources().getString(R.string.produtoscarregadosdopropriocelular);

            Toast.makeText(MyApp.getContext(), mensagem + "\n" + mensagem2, Toast.LENGTH_LONG).show();
            isForcarAtualizacao = false;
        }

        if (isForcarAtualizacao && sServidorIP != null && !sServidorIP.isEmpty()) {
            HttpHelper helper = new HttpHelper();
            json = helper.doGet("http://" + sServidorIP + "/ddm-produtos.json");
            bGet = true;
        } else {
            json = getJsonConfiguracao();
        }

        if (json == null || json.isEmpty()) {
            if (sServidorIP == null || sServidorIP.isEmpty()) {
                throw new IOException("IP do servidor não configurado!\nNenhum produto cadastrado no celular.");
            } else {
                throw new IOException("Nenhum produto cadastrado no celular.");
            }
        } else {
            produtos = parserJSON(json);

            if (produtos == null || produtos.isEmpty()) {
                produtos = getListaProdutosConfiguracao();
            }
            else {
                if (bGet) {
                    ConfigSharedPreferences.setString(MyApp.getContext(), "cfgJsonProdutos", json);
                    ConfigSharedPreferences.setString(MyApp.getContext(), "cfgDtUltAtz", DateUtil.DataDMY());
                }
            }
        }

        return produtos;
    }

    private static String getJsonConfiguracao() {
        String json = ConfigSharedPreferences.getString(MyApp.getContext(), "cfgJsonProdutos");
        return json;
    }

    public static List<Produto> getListaProdutosConfiguracao() throws IOException {
        List<Produto> produtos = null;

        String json = getJsonConfiguracao();

        if (json == null || json.isEmpty()) {
            throw new IOException("Nenhum produto cadastrado no celular.");
        }

        produtos = parserJSON(json);

        return produtos;
    }

    private static List<Produto> parserJSON(String json) {
        List<Produto> produtos = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(json);
            JSONObject obj = root.getJSONObject("produtos");
            JSONArray jsonProdutos = obj.getJSONArray("produto");

            for (int i = 0; i < jsonProdutos.length(); i++) {
                JSONObject jsonProduto = jsonProdutos.getJSONObject(i);
                Produto p = new Produto();

                p.setEan(jsonProduto.optString("ean"));
                p.setDescricao(jsonProduto.optString("descricao"));
                p.setPcovenda(jsonProduto.optDouble("pcovenda"));

                produtos.add(p);
            }
        } catch (JSONException e) {
            produtos.clear();
        }

        return produtos;
    }
}
