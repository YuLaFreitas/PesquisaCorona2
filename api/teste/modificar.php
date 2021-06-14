<?php
//VARIAVEIS
header('Content-Type: application/json; charset=utf-8');  

$table = $_GET["tabela"].$_POST["tabela"];

$acao = $_GET["do"].$POST["do"];
$long = $_GET["long"].$POST["long"];
$lat = $_GET["lat"].$POST["lat"];

$date = $_GET["data"].$POST["data"];
$confirmado = $_GET["casos"].$POST["casos"];
$mortos = $_GET["mortes"].$POST["mortes"];

$json = [];
$sql = "";


//CONEXÃO COM BANCO DE DADOS

    $local = "localhost";
    $username= "u309547285_apiSaude";
    $senhadb = "YuL@Fr31";

  $conexao = mysqli_connect($local, $username, $senhadb, $username);
   $id;
//DESVIOS

switch ($acao) {
    case 'criar':
        
        $sql_teste = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'dados_gerais' AND table_name = 'dados_gerais';";
        
        $sql_estrutura = 
        'CREATE TABLE dados_gerais ('+
            +'id int(11) primary key auto_incriment,'
            +'casos int(20),'
            +'data date,'
            +'mortos int int(11)'
            +');'+
            'CREATE TABLE historico ('+
            +'id int(11) primary key auto_incriment,'
            +'data date,'
            +'casos int(20),'
            +'mortos int int(11),'
            +'longitude varchar(20),'
            +'latitude varchar(20)'
            +');';
            
        if(mysqli_query($conexao, $sql_estrutura) == false){
        mysqli_query($conexao, $sql_estrutura);}
            
        $url = "https://api.covid19api.com/country/brazil?from=2020-12-01T00:00:00Z&to=20".date('y-m-d')."T00:00:00Z";
            
        $json_file = file_get_contents($url);
        
        $json_str = json_decode($json_file, true);
        
        $itens = $json_str; 
    
        $i = 0;
        foreach ($json_str as $e ) 
    { $caso = $e['Confirmed'];
      $mort = $e['Deaths'];
      $data = $e['Date'];
      $sql_inserir ="INSERT INTO dados_gerais VALUES('','{$caso}','{$data}','{$mort}');";
        mysqli_query($conexao, $sql_inserir);      
     // echo "Casos: $caso, mortes: $mort, data: $data\n";
      
    } 
    
    
        
     $json = ['status'=>1,'mensagem' => 'Sucesso'];
   
            break;    

  case 'finalizarPesquisa':
      //('id', 'casos', 'data', 'mortos', 'longitude', 'latitude')
      $sql = "INSERT INTO historico  VALUES('','{$date}','{$casos}','{$mortos}','{$long}','{$lat}');";
    
   
     $json = ['status'=>1,'mensagem' => 'Sucesso'];
   
      break;

  default: 
      echo 'erro aqui';
      break;
}

//testar conexão
if (!$result = $conexao -> query($sql)) {  $json = ['status'=>2,'mensagem' => 'Verificar conexão'.mysqli_connect_error(), 'id' => 0];}

echo json_encode($json);
mysqli_close($conexao);
//?>