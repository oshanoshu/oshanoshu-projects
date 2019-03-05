var botui = new BotUI('chatbot');
var custName;
var video_link=['<iframe width="644" height="362" src="https://www.youtube.com/embed/f_d5ZJtTGLM" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>','<iframe width="644" height="362" src="https://www.youtube.com/embed/nPsz2ggTAkk" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>','<iframe width="644" height="362" src="https://www.youtube.com/embed/pOmu0LtcI6Y" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>','<iframe width="644" height="362" src="https://www.youtube.com/embed/_ZfrKpNkRlA" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>'];
var winCount=0;
var total=0;

//For taking off
function hello(){botui.message.add({
  delay: 300,
  loading: true,
  content: 'Hello There!'
}).then(function () { // wait till previous message has been shown.

  botui.action.text({
    action: {
      placeholder: 'Enter your text here'
    }
  }).then(function (res) { // will be called when it is submitted.
    console.log(res.value); // will print whatever was typed in the field.
}).then(function(){
  botui.message.add({
    delay: 300,
    loading: true,
    content: 'What is your name?'
  })
}).then(function(){
  return botui.action.text({
    action: {
      placeholder: 'your name'
    }
  });
}).then(function (res) { // will be called when it is submitted.
  custName=res.value;
  botui.message.add({
    content: 'Your name is ' + custName +'. That is a sweet name'
  })
}).then(function (){
  botui.message.add({
    delay:500,
    loading: true,
    content: 'Hey, '+' Would you like to play a fun game?'
  })
}).then(function(){
  return botui.action.button({
    action:[
      {
        text:'Sure, I would like to play',
        value:'yes'
      },
      {
        text:'Nah, I am bored',
        value: 'no'
      }
    ]
  });
}).then(function(res){
  if(res.value=='yes')
  {
    gameTime();
  }
  else {
    {
      watchVideo();
    }
  }

});
});
}
//For game
function gameTime(){
  botui.message.add({
    delay:1100,
    loading:true,
    content: 'Be ready to rock and roll'
  }).then(function(){
    return botui.action.button({
      action: [
        {
          text: 'Coin toss',
          value: 1
        },
        {
          text: 'Roll the dice',
          value: 2
        }
      ]

    });
  }).then(function(res){
    if(res.value==1)
    {
      coinToss();//Calls the coin toss game
    }
    else{
      rollTheDice();//Call the roll the dice game
    }
  });
}

//For watching video
function watchVideo(){
  botui.message.add({
    delay:1100,
    loading:true,
    content: 'Do you want to watch a video that might cheer you up?'
  }).then(function(){
    return botui.action.button({
      action: [
        {
          text:'Yes, I wanna try',
          value: 'yes'
        },
        {
          text: 'Nah!!!',
          value: 'no'
        }
      ]
    });
  }).then(function(res){
    var randomNum=Math.floor(Math.random()*4);
    if(res.value=='yes')
    {
      botui.message.add({
        type: 'html',
        content: video_link[randomNum]
      }).then(function(){
        botui.message.add({
          content: 'Did you like the video?'
        })

      }).then(function(){
        return botui.action.button({
          action: [
            {
              delay: 12000,
              text: 'Yeah!',
              value: 'yes'
            },
            {
              delay: 12000,
              text: 'Nah!!',
              value: 'no'
            }
          ]
        });
      }).then(function(res){
          if(res.value=='yes')
          {
            botui.message.add({
              content: 'If you like it, watch more on [YouTube](https://www.youtube.com)^'
            })
          }
          else
          {
            botui.message.add({
              content: 'I think you need to get some rest, '+custName +'. Talk to you later.'
            })
          }

      });
    }
    else{
      botui.message.add({
        content: 'I think you need to get some rest, '+custName +'. Talk to you later.'
      })
    }
  });
};
//For coinToss
function coinToss(){
  //var flip=Math.floor(Math.random()*2);
  botui.message.add({
    delay: 500,
    loading: true,
    content: 'I am tossing the coin. Please make a call'
  }).then(function(){
    return botui.action.button({
     action: [
       {
         text: 'Head',
         value: 0
       },
       {
         text: 'Tail',
         value: 1
       }
     ]
   });
  }).then(function(res){
    total++;
    let coin=['heads','tails'];
    let flip=Math.floor(Math.random()*2);
    if(flip==res.value){//win situation
      winCount++;
      stats();//Calls the function to record win
    botui.message.add({
      content: 'Yes!, it is '+coin[flip]+ '. Congrats, you win'
    })
  }
    else{//lose situation
      stats();
      botui.message.add({
        content: 'No!, it is '+coin[flip]+ '. Sorry, you lose'
      })
    }
  }).then(function(){//Provides option for playing again
    botui.message.add({
      content: 'Do you wanna play again?'
    })
  }).then(function(){
    return botui.action.button({
      action: [
        {
          text: 'Yes',
          value: 0
        },
        {
          text: 'No',
          value: 1
        }
      ]
    });
  }).then(function(res){
    if(res.value==0)
    {
      coinToss(); //recursive
    }
    else
    {
      menu();//Calls the menu
    }
  });
}
//For rolling the dice
function rollTheDice(){
  botui.message.add({
    delay: 600,
    loading: true,
    content: 'I am rolling the dice. Please make a call'
  }).then(function(){
   return botui.action.button({
    action: [
      {
        text: 'One',
        value: 1
      },
      {
        text: 'Two',
        value: 2
      },
      {
        text: 'Three',
        value: 3
      },
      {
        text: 'Four',
        value: 4
      },
      {
        text: 'Five',
        value: 5
      },
      {
        text: 'Six',
        value: 6
      }
    ]
  });
  }).then(function(res){
    total++;
    let dice=[1,2,3,4,5,6];
    let roll=Math.floor(Math.random()*6);
    if(dice[roll]==res.value){//winS situation
      winCount++;
      stats();
    botui.message.add({
      content: 'Yes!, it is '+dice[roll]+ '. Congrats, you win'
    })
  }
    else{
      stats();
      botui.message.add({
        content: 'No!, it is '+dice[roll]+ '. Sorry, you lose'
      })
    }
  }).then(function(){//Provides option for playing again
    botui.message.add({
      content: 'Do you wanna play again?'
    })
  }).then(function(){
    return botui.action.button({
      action: [
        {
          text: 'Yes',
          value: 0
        },
        {
          text: 'No',
          value: 1
        }
      ]
    });
  }).then(function(res){
    if(res.value==0)
    {
      rollTheDice(); //recursive
    }
    else
    {
      menu();//Calls the menu
    }
  });
}
//Records the stats of the player playing
function stats(){
  var element=document.getElementById('stats');
  if(total>1){
  element.innerHTML="You have won "+winCount+" out of "+ total+" matches!";
}
  else{
    element.innerHTML="You have won "+winCount+" out of "+ total+" match!";
  }
}
function menu(){//MENU
  botui.message.add({
    delay: 1000,
    loading: true,
    content: 'What would you like to do '+custName+'?'
  }).then(function(){
    return botui.action.button({
      action:[
        {
          text: 'Play Coin Toss',
          value: 1
        },
        {
          text: 'Play Roll The Dice',
          value: 2
        },
        {
          text: 'Watch Video',
          value: 3
        },
        {
          text: 'Exit the conversation',
          value: 4
        }

      ]
    });
  }).then(function(res){
    if(res.value==1)
    {
      coinToss();
    }
    else if (res.value==2) {
      rollTheDice();
    }
    else if (res.value==3) {
      watchVideo();
    }
    else{
      botui.message.add({
        delay: 1000,
        loading: true,
        content: 'Nice talking to you, '+custName
      })
    }
  });
}
hello()
