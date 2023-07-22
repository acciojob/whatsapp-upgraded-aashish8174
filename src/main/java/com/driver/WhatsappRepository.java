package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {
    
    // user db
    Map<String ,String> userDB = new HashMap<>();// user mobile , name

    Map<String,List<User>> groupUserDB = new HashMap<>(); // pri =>group name,user
    Map<String ,User> groupAdminDB = new HashMap<>(); // prim =>group name ,admin

    Map<Integer,Message> idMassageDb = new HashMap<>();

    Map<String,List<Message>> groupMassageDB = new HashMap<>();
    Map<User,List<Message>> userMassageDB = new HashMap<>();

    public String creatUser(String name, String mobile) {
        if(userDB.containsKey(mobile)){
            throw new RuntimeException("User already exist");
        }
        userDB.put(mobile,name);
        return "SUCCESS";
    }

    int i = 0;
    public Group createGroup(List<User> users) {

        if(users.size()==2){
            groupUserDB.put(users.get(1).getName(),users);
            Group group = new Group();
            group.setName(users.get(1).getName());
            group.setNumberOfParticipants(2);

            groupAdminDB.put(group.getName(),users.get(1));
            return group;
        }
        else{
            i++;
            String name ="Group "+i;

            groupUserDB.put( name,users);
            Group group = new Group();
            group.setName(name);
            group.setNumberOfParticipants(users.size());

            groupAdminDB.put(group.getName(),users.get(0));
            return group;
        }

    }

    public int createMassage(String content) {
        Message message = new Message();
        int id = idMassageDb.size()+1;
        message.setId(id);
        message.setContent(content);
        Date date = Calendar.getInstance().getTime();
        message.setTimestamp(date);
        idMassageDb.put(id,message);
        return id;
    }

    public int sendMassage(Message message, User sender, Group group) {
        if(!groupAdminDB.containsKey(group.getName())){
            throw new RuntimeException( "Group does not exist" );
        }
        List<User> userList = groupUserDB.get(group.getName());
        for (User u : userList){
            if(u.equals(sender)){
                List<Message> l = groupMassageDB.get(group);
                l.add(message);
                groupMassageDB.put(group.getName(),l);
                List<Message>usermessage = userMassageDB.get(sender);
                usermessage.add(message);
                userMassageDB.put(sender,usermessage);
               return groupMassageDB.get(group).size();

            }
        }
        throw new RuntimeException("You are not allowed to send message");
    }

    public String changeAdmin(User approver, User user, Group group) {
        if(!groupUserDB.containsKey(group.getName())){
            throw new RuntimeException( "Group does not exist" );
        }
        if(!groupAdminDB.get(group.getName()).equals(approver)){
            throw new RuntimeException("Approver does not have rights");
        }
        List<User> users = groupUserDB.get(group.getName());
        boolean userFound = false;
        for (User user1 : users){
            if(user1.equals(user)){
                userFound = true;
            }
        }
        if(!userFound){
            throw new RuntimeException("User is not a participant");
        }

        User Oldadmin = groupAdminDB.get(group.getName());
        groupAdminDB.put(group.getName(),user);
        return "SUCCESS";

    }

    public int removeUser(User user) {
        int groupMessage = 0;
        int overAllMessage = 0;
        int updatedUsers = 0;
        for(String groupN:groupUserDB.keySet()){
            List<User> list = groupUserDB.get(groupN);
            for (User user1:list){
                if (user1.equals(user)){
                  if(groupAdminDB.get(groupN).equals(user)){ // user is admin
                      throw new RuntimeException("Cannot remove admin");
                  }
                    list.remove(user1);
                    List<Message> messageList = userMassageDB.get(user);
                    userMassageDB.remove(user1);
                    List<Message> messageLis1 =  groupMassageDB.get(groupN);

                    for (Message m : messageList){
                        messageLis1.remove(m);
                    }
                    groupMassageDB.put(groupN,messageLis1);
                    groupMessage = messageLis1.size();
                }
                updatedUsers= list.size();
            }
        }
        for (String g : groupMassageDB.keySet()){
            overAllMessage+=groupMassageDB.get(g).size();
        }
        return overAllMessage+groupMessage+updatedUsers;
    }

    public String findMessage(Date start, Date end, int k) {
        return "";
    }
}
