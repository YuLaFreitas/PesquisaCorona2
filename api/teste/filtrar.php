<?php
//VARIAVEIS

$APELIDO = $_GET["apelido"].$_POST["apelido"]; 
$NAME = $_GET["nome"].$_POST["nome"]; 
$EMAIL = $_GET["email"].$_POST["email"];
$SENHA = $_GET["senha"].$_POST["senha"];

$BAIRRO = $_GET["bairro"].$_POST["bairro"];
$CIDADE = $_GET["cidade"].$_POST["cidade"];
$RUA = $_GET["logradura"].$_POST["logradura"];
$NUMERO = $_GET["numero"].$_POST["numero"];

$NASCIMENTO = $_GET["nascimento"].$_POST["nascimento"];
$DOENCA = $_GET["doenca"].$_POST["doenca"];

$nivel = $_GET["nivel"].$_POST["nivel"];
$idcidadao = $_GET["idcidadao"].$_POST["idcidadao"];

$table = $_GET["tabela"].$_POST["tabela"];
$pesquisa = $_GET["pesquisa"].$_POST["pesquisa"];
$coluna = $_GET["coluna"].$_POST["coluna"];

$json = [];
$sql = "";
$vacina; $idadeMax; $idadeMin; $dataInicial; $dataFim; $previne;$texto = "\n\n\n";
//CONEXÃO COM BANCO DE DADOS

    $local = "localhost";
    $username= "u309547285_apiSaude";
    $senhadb = "YuL@Fr31";
    


  $conexao = mysqli_connect($local, $username, $senhadb, $username);
  
 
//DESVIOS

switch ($table) {
    
 case 'dados_gerais':
     
     $sql = "SELECT * FROM {$table} WHERE {$coluna} LIKE '{$pesquisa}'";
     
     $result = mysqli_query($conexao,$sql);
      
    //$res = mysqli_num_rows($result);
    
    while($row = mysqli_fetch_array($result)){
     $word = $row['casos'];
    // $day = $row['data'];
     }
    
       $json = ['q' => $word*1];
      
      break;
    
  case 'sintoma':
     
     $sql = "SELECT * FROM {$table} WHERE {$coluna} LIKE '{$pesquisa}'";
     
     $result = mysqli_query($conexao,$sql);
      
       $res = mysqli_num_rows($result);
       
       $json = ['q' => $res];
      
      break;

   case 'vacinacao':

   $sql = "SELECT * FROM {$table}";
  
  if($res = mysqli_query($conexao,$sql)){
     while($row = mysqli_fetch_array($res)){
         $i = 0;
         $vacina = $row['vacina']; 
         $idadeMax= $row['idadeMax']; 
         $idadeMin = $row['idadeMin']; 
         $dataInicial = $row['dataInicial']; 
         $dataFim = $row['dataFim'];
         $previne = $row['prevencoes'];
     
     $texto .= " VACINA: {$vacina} \n DE: {$idadeMin} ANOS\nATÉ {$idadeMax} ANOS \n COMEÇA EM {$dataInicial} \n E IRÁ ATÉ {$dataFim} \n PREVINIRÁ {$previne}\n-------------\n"; 
     $array .= ['vacina' => $vacina, 'idMax' ];
         $i++;     
     }
     
     if($id != null)
     {     $json = ['status'=>1,'mensagem' => 'Sucesso', 'id' => $texto];  }
     else{     $json = ['status'=>2,'mensagem' => 'Queria verificar a senha', 'id' => $texto];  }
     
  }
      $json = ['q' => $texto];
  
     
    break;


  case 'registrar_pesquisa':
        $data = date('d/m/y');
       $sql = "INSERT INTO {$table} VALUES ('','{$nivel}','{$idcidadao}','{$sintoma}','{$data}');";
       $json = ['status'=>1,'mensagem' => 'Sucesso'];
      // echo json_encode($json);
      break;

  case 'inserir':
      $sql = "INSERT INTO {$table} VALUES ('','{$NASCIMENTO}','{$NAME}','{$APELIDO}','{$DOENCA}','{$EMAIL}','{$SENHA}','{$BAIRRO}','{$RUA}','{$CIDADE}','{$NUMERO}');";
   // mysqli_query($conexao,$sql);
     $json = ['q'=>1,'mensagem' => 'Sucesso'];
    // echo json_encode($json);
            break;

  case 'DELETE':
      $sql = "delete from `$table` where id=$key";
     $json = ['q'=>1,'mensagem' => 'Sucesso'];
 // echo json_encode($json);
  break;
  default: 
      echo 'Problema com a opcao';
}

$result = mysqli_query($conexao,$sql);


//testar conexão
if ($result = $conexao -> query($sql)) {
  if(($result -> num_rows) >= 0){
    
    // $json = ['status'=>1,'mensagem' => 'Sucesso'];
           //    echo json_encode($json);
  }
  else{
      $json = ['q'=>0,'mensagem' => 'Não encontrado'];
      echo json_encode($json);
  }
}/*else{
    $json = ['status'=>2,'mensagem' => 'Verificar conexão'];
    
}*/

echo json_encode($json);
mysqli_close($conexao);
?>